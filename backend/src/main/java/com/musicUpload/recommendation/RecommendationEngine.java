package com.musicUpload.recommendation;

import com.musicUpload.dataHandler.details.UserDetailsImpl;
import com.musicUpload.dataHandler.enums.ProtectionType;
import com.musicUpload.dataHandler.models.implementations.Album;
import com.musicUpload.dataHandler.models.implementations.Song;
import com.musicUpload.dataHandler.models.implementations.Tag;
import com.musicUpload.dataHandler.models.implementations.UserSong;
import com.musicUpload.dataHandler.repositories.AlbumRepository;
import com.musicUpload.dataHandler.repositories.SongRepository;
import com.musicUpload.dataHandler.repositories.UserRepository;
import com.musicUpload.dataHandler.services.UserService;
import com.musicUpload.dataHandler.services.UserSongService;
import com.musicUpload.exceptions.NotFoundException;
import com.musicUpload.exceptions.UnauthenticatedException;
import com.musicUpload.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    private final Map<Integer, Double> weekToMultiplierMap;

    @Autowired
    public RecommendationEngine(UserSongService userSongService,
                                SongRepository songRepository,
                                UserRepository userRepository,
                                AlbumRepository albumRepository) {
        this.userSongService = userSongService;
        this.songRepository = songRepository;
        this.userRepository = userRepository;
        this.albumRepository = albumRepository;
        this.weekToMultiplierMap = Map.of(
                1, 1.0,
                2, 0.8,
                3, 0.6,
                4, 0.5
        );
    }

    private Map<Long, Double> getSongsWithWeightedOccurrenceCount(Long songId, Long userId, int weeksBefore) {

        if (weeksBefore < 1) {
            throw new RuntimeException();
        }

        double multiplier = weekToMultiplierMap.getOrDefault(weeksBefore, 0.25);
        Date dateGivenWeeksAgoStart = getDateWeeksFromToday(weeksBefore);
        Date dateGivenWeeksAgoEnd = weeksBefore == 1 ? new Date() : getDateWeeksFromToday(weeksBefore - 1);

        Optional<Song> songOpt = songRepository.findByIdAndProtectionTypeInOrUser(
                songId,
                userId,
                List.of(ProtectionType.PUBLIC, ProtectionType.PROTECTED)
        );

        if (songOpt.isEmpty()) {
            return Map.of();
        }

        Song s = songOpt.orElseThrow(UnauthenticatedException::new);

        Set<Long> tagsForSong = s.getTags().stream().map(Tag::getId).collect(Collectors.toSet());

        Set<UserSong> usersWhoListenedToTheSong = userSongService.getListensForSongAndCreatedAtGreaterThan(
                songId, dateGivenWeeksAgoStart, dateGivenWeeksAgoEnd
        );

        //songs with the same tag
        Set<UserSong> songsOfUsersWhoListenedToTheSong = userSongService.getSongsForUsersAndCreatedAtGreaterThan(
                usersWhoListenedToTheSong
                        .stream()
                        .parallel()
                        .map(UserSong::getUserId)
                        .filter(id -> !id.equals(userId))
                        .collect(Collectors.toSet()),
                tagsForSong,
                dateGivenWeeksAgoStart,
                dateGivenWeeksAgoEnd
        );

        Map<Long, Song> alreadyQueriedSongs = new ConcurrentHashMap<>();
        Map<Long, Double> songOccurrenceMap = new ConcurrentHashMap<>();
        songsOfUsersWhoListenedToTheSong.stream().parallel()
                .forEach(userSong -> {
                    var song = alreadyQueriedSongs.get(userSong.getSongId());

                    if (song == null) {
                        song = songRepository.findById(userSong.getSongId()).orElse(null);
                        if (song != null) {
                            alreadyQueriedSongs.put(songId, song);
                        }
                    }

                    if (song != null) {
                        songOccurrenceMap.merge(song.getId(), multiplier, Double::sum);
                    }
                });
        return songOccurrenceMap;
    }

    public List<Long> createRecommendationsForSong(Long songId) {
        Long userId = Optional.ofNullable(UserService.getCurrentUserDetails())
                .map(UserDetailsImpl::getId)
                .orElse(0L);
        try (ExecutorService executorService = Executors.newFixedThreadPool(4)) {
            return IntStream.range(0, 3)
                    .mapToObj(i -> executorService.submit(
                                    () -> getSongsWithWeightedOccurrenceCount(songId, userId, i + 1)
                                            .entrySet()
                                            .stream()
                            )
                    )
                    .map(future -> {
                        try {
                            return future.get();
                        } catch (ExecutionException | InterruptedException e) {
                            log.error("Exception happened: {}", e.getMessage());
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .flatMap(Stream::parallel)
                    .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    Map.Entry::getValue,
                                    Double::sum
                            )
                    ).entrySet()
                    .stream()
                    .map(Pair::new)
                    .sorted((p1, p2) -> Double.compare(p2.getSecond(), p1.getSecond()))
                    .map(Pair::getFirst)
                    .filter(id -> !id.equals(songId))
                    .toList();
        }
    }

    public List<Long> createRecommendationsForUser(Long userId) {
        log.info("creating recommendation for user {}", userId);
        long start = System.currentTimeMillis();

        if (userId.equals(0L)) {
            return songRepository.findByProtectionTypeOrderByListenCount(
                ProtectionType.PUBLIC,
                NON_AUTHENTICATED_USER_LIMIT
            );
        }

        userRepository.findById(userId)
                .orElseThrow(UnauthenticatedException::new);

        Set<UserSong> songs = userSongService.getSongsForUser(
                userId,
                getDateWeeksFromToday(1),
                new Date()
        );

        Map<Long, Double> songOccurrences = new ConcurrentHashMap<>();
        songs.stream().parallel().forEach(userSong -> {
            Long songId = userSong.getSongId();
            try (ExecutorService executorService = Executors.newFixedThreadPool(4)) {
                Map<Long, Double> mapForSong = IntStream.range(0, 3)
                        .mapToObj(i -> executorService.submit(
                                        () -> getSongsWithWeightedOccurrenceCount(songId, userId, i + 1)
                                                .entrySet()
                                                .stream()
                                )
                        )
                        .map(future -> {
                            try {
                                return future.get();
                            } catch (ExecutionException | InterruptedException e) {
                                log.error("Exception happened: {}", e.getMessage());
                            }
                            return null;
                        })
                        .filter(Objects::nonNull)
                        .flatMap(Stream::parallel)
                        .collect(Collectors.toConcurrentMap(
                                        Map.Entry::getKey,
                                        Map.Entry::getValue,
                                        Double::sum
                                )
                        );
                mapForSong.entrySet().stream().parallel().forEach(
                        entry -> songOccurrences.merge(entry.getKey(), entry.getValue(), Double::sum)
                );
            }
        });

        log.info("finished creating recommendation for user {} in {}", userId, (System.currentTimeMillis() - start)/1_000);
        return sortMap(songOccurrences);
    }

    public List<Long> createRecommendationsForAlbum(Long albumId) {
        Long userId = Optional.ofNullable(UserService.getCurrentUserDetails())
                .map(UserDetailsImpl::getId)
                .orElse(0L);

        Album a = albumRepository.findById(albumId)
                .orElseThrow(NotFoundException::new);

        List<Song> songs = a.getSongs();

        Map<Long, Double> songOccurrences = new ConcurrentHashMap<>();
        songs.stream().parallel().forEach(song -> {
            Long songId = song.getId();
            try (ExecutorService executorService = Executors.newFixedThreadPool(4)) {
                Map<Long, Double> mapForSong = IntStream.range(0, 3)
                        .mapToObj(i -> executorService.submit(
                                        () -> getSongsWithWeightedOccurrenceCount(songId, userId, i + 1)
                                                .entrySet()
                                                .stream()
                                )
                        )
                        .map(future -> {
                            try {
                                return future.get();
                            } catch (ExecutionException | InterruptedException e) {
                                log.error("Exception happened: {}", e.getMessage());
                            }
                            return null;
                        })
                        .filter(Objects::nonNull)
                        .flatMap(Stream::parallel)
                        .collect(Collectors.toConcurrentMap(
                                        Map.Entry::getKey,
                                        Map.Entry::getValue,
                                        Double::sum
                                )
                        );
                mapForSong.entrySet().stream().parallel().forEach(
                        entry -> songOccurrences.merge(entry.getKey(), entry.getValue(), Double::sum)
                );
            }
        });

        return sortMap(songOccurrences);
    }

    private List<Long> sortMap(Map<Long, Double> songOccurrences) {
        return songOccurrences
                .entrySet()
                .stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .map(Map.Entry::getKey)
                .toList();
    }

    private Date getDateWeeksFromToday(int weeksAgo) {
        return Date.from(
                LocalDate.now()
                        .minusWeeks(weeksAgo)
                        .atStartOfDay()
                        .toInstant(
                                ZoneOffset.UTC
                        )
        );
    }
}
