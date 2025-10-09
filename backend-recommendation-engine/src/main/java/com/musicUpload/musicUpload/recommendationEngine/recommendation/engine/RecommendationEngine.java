package com.musicUpload.musicUpload.recommendationEngine.recommendation.engine;

import com.musicUpload.musicUpload.recommendationEngine.database.entity.*;
import com.musicUpload.musicUpload.recommendationEngine.database.repository.AlbumRepository;
import com.musicUpload.musicUpload.recommendationEngine.database.repository.SongRepository;
import com.musicUpload.musicUpload.recommendationEngine.database.repository.UserRepository;
import com.musicUpload.musicUpload.recommendationEngine.database.service.UserSongService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.musicUpload.musicUpload.recommendationEngine.exceptions.UnauthenticatedException;
import com.musicUpload.musicUpload.recommendationEngine.exceptions.NotFoundException;
import com.musicUpload.musicUpload.recommendationEngine.util.Pair;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@Slf4j
public class RecommendationEngine {
    private final long NON_AUTHENTICATED_USER_LIMIT = 100L;
    private final UserSongService userSongService;
    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final AlbumRepository albumRepository;
    private final Map<Integer, Double> monthToMultiplierMap;

    @Autowired
    public RecommendationEngine(UserSongService userSongService,
                                SongRepository songRepository,
                                UserRepository userRepository,
                                AlbumRepository albumRepository) {
        this.userSongService = userSongService;
        this.songRepository = songRepository;
        this.userRepository = userRepository;
        this.albumRepository = albumRepository;
        this.monthToMultiplierMap = Map.ofEntries(
                Map.entry(1, 1.0),
                Map.entry(2, 0.8),
                Map.entry(3, 0.6),
                Map.entry(4, 0.5),
                Map.entry(5, 0.4),
                Map.entry(6, 0.3),
                Map.entry(7, 0.25),
                Map.entry(8, 0.25),
                Map.entry(9, 0.25),
                Map.entry(10, 0.2),
                Map.entry(11, 0.2),
                Map.entry(12, 0.1)
        );
    }

    private Stream<Map.Entry<Long, Double>> getSongsWithWeightedOccurrenceCount(Long songId, Long userId, int monthsBefore) {

        if (monthsBefore < 1) {
            throw new RuntimeException();
        }

        double multiplier = monthToMultiplierMap.getOrDefault(monthsBefore, 0.1);
        Date dateGivenMonthsAgoStart = getDateMonthsFromToday(monthsBefore);
        Date dateGivenMonthsAgoEnd = monthsBefore == 1 ? new Date() : getDateMonthsFromToday(monthsBefore - 1);

        Optional<Song> songOpt = songRepository.findByIdAndProtectionTypeInOrUser(
                songId,
                userId,
                List.of(ProtectionType.PUBLIC, ProtectionType.PROTECTED)
        );

        if (songOpt.isEmpty()) {
            return Stream.of();
        }

        Song s = songOpt.orElseThrow(UnauthenticatedException::new);

        Set<Long> tagsForSong = s.getTags().stream().map(Tag::getId).collect(Collectors.toSet());

        Set<UserSong> usersWhoListenedToTheSong = userSongService.getListensForSongAndCreatedAtGreaterThan(
                songId, dateGivenMonthsAgoStart, dateGivenMonthsAgoEnd
        );

        //songs with the same tag
        return userSongService.getSongsForUsersAndCreatedAtGreaterThan(
                        usersWhoListenedToTheSong
                                .stream()
                                .parallel()
                                .map(UserSong::getUserId)
                                .filter(id -> !id.equals(userId))
                                .collect(Collectors.toSet()),
                        tagsForSong,
                        dateGivenMonthsAgoStart,
                        dateGivenMonthsAgoEnd
                ).stream().collect(Collectors.groupingBy(UserSong::getSongId)).entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), multiplier * entry.getValue().size()));
    }

    public List<Long> createRecommendationsForSong(Long songId, Long userId) {
        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            var sortedSongsIds = IntStream.range(0, 11)
                    .mapToObj(i -> executorService.submit(
                            () -> getSongsWithWeightedOccurrenceCount(songId, userId, i + 1)))
                    .map(future -> {
                        try {
                            return future.get();
                        } catch (ExecutionException | InterruptedException e) {
                            logError(e);
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .flatMap(Function.identity())
                    .peek(o -> log.info("{}", o))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            Double::sum,
                            LinkedHashMap::new))
                    .entrySet()
                    .stream()
                    .map(Pair::new)
                    .sorted((p1, p2) -> Double.compare(p2.getSecond(), p1.getSecond()))
                    .map(Pair::getFirst)
                    .filter(id -> !id.equals(songId))
                    .toList();

            sortedSongsIds = userSongService.filterOutRestrictedSongs(userId, sortedSongsIds);

            if (sortedSongsIds.size() < 20) {
                return Stream.concat(sortedSongsIds.stream(),
                        defaultRecommendations(
                                20L - sortedSongsIds.size(),
                                Stream.concat(sortedSongsIds.stream(), Stream.of(songId)).toList()).stream()).toList();
            }
            return sortedSongsIds;
        }
    }

    public List<Long> createRecommendationsForUser(Long userId) {
        log.info("creating recommendation for user {}", userId);
        long start = System.currentTimeMillis();

        if (userId.equals(0L)) {
            return defaultRecommendations();
        }

        userRepository.findById(userId).orElseThrow(UnauthenticatedException::new);

        Set<UserSong> songs = userSongService.getSongsForUser(
                userId,
                getDateMonthsFromToday(1),
                new Date()
        );

        Map<Long, Double> songOccurrences = new ConcurrentHashMap<>();
        songs.stream().parallel().forEach(userSong -> {
            Long songId = userSong.getSongId();
            try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
                Map<Long, Double> mapForSong = IntStream.range(0, 11)
                        .mapToObj(i -> executorService.submit(
                                () -> getSongsWithWeightedOccurrenceCount(songId, userId, i + 1)))
                        .map(future -> {
                            try {
                                return future.get();
                            } catch (ExecutionException | InterruptedException e) {
                                logError(e);
                            }
                            return null;
                        })
                        .filter(Objects::nonNull)
                        .flatMap(Function.identity())
                        .collect(Collectors.toConcurrentMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                Double::sum));
                mapForSong.entrySet().stream().parallel().forEach(
                        entry -> songOccurrences.merge(entry.getKey(), entry.getValue(), Double::sum)
                );
            }
        });

        log.info("finished creating recommendation for user {} in {}", userId, (System.currentTimeMillis() - start) / 1_000);
        List<Long> sortedSongsIds = sortMap(songOccurrences);
        sortedSongsIds = userSongService.filterOutRestrictedSongs(userId, sortedSongsIds);
        if (sortedSongsIds.size() < 20) {
            return Stream.concat(
                    sortedSongsIds.stream(),
                    defaultRecommendations(20L - sortedSongsIds.size(), sortedSongsIds).stream()).toList();
        }
        return sortedSongsIds;
    }

    public List<Long> createRecommendationsForAlbum(Long albumId, Long userId) {
        Album a = albumRepository.findById(albumId)
                .orElseThrow(NotFoundException::new);

        List<Song> songs = a.getSongs();

        Map<Long, Double> songOccurrences = new ConcurrentHashMap<>();
        songs.stream().parallel().forEach(song -> {
            Long songId = song.getId();
            try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
                Map<Long, Double> mapForSong = IntStream.range(0, 11)
                        .mapToObj(i -> executorService.submit(
                                () -> getSongsWithWeightedOccurrenceCount(songId, userId, i + 1)))
                        .map(future -> {
                            try {
                                return future.get();
                            } catch (ExecutionException | InterruptedException e) {
                                logError(e);
                            }
                            return null;
                        })
                        .filter(Objects::nonNull)
                        .flatMap(Function.identity())
                        .collect(Collectors.toConcurrentMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                Double::sum));
                mapForSong.entrySet().stream().parallel().forEach(
                        entry -> songOccurrences.merge(entry.getKey(), entry.getValue(), Double::sum)
                );
            }
        });

        List<Long> sortedSongsIds = sortMap(songOccurrences);
        sortedSongsIds = userSongService.filterOutRestrictedSongs(userId, sortedSongsIds);
        if (sortedSongsIds.size() < 20) {
            return Stream.concat(
                    sortedSongsIds.stream(),
                    defaultRecommendations(20L - sortedSongsIds.size(), sortedSongsIds).stream()).toList();
        }
        return sortedSongsIds;
    }


    public List<Long> defaultRecommendations(Long limit, List<Long> excludedSongIds) {
        return songRepository.findByProtectionTypeOrderByListenCount(
                ProtectionType.PUBLIC, limit, excludedSongIds
        );
    }

    public List<Long> defaultRecommendations() {
        return defaultRecommendations(NON_AUTHENTICATED_USER_LIMIT, List.of());
    }

    private List<Long> sortMap(Map<Long, Double> songOccurrences) {
        return songOccurrences
                .entrySet()
                .stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private Date getDateMonthsFromToday(int monthsAgo) {
        return Date.from(
                LocalDate.now()
                        .minusMonths(monthsAgo)
                        .atStartOfDay()
                        .toInstant(ZoneOffset.UTC)
        );
    }

    private void logError(Exception e) {
        log.error("Exception happened: {}", e.getMessage());
    }
}
