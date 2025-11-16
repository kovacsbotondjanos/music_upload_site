package com.musicUpload.musicUpload.recommendationEngine.database.service;

import com.musicUpload.musicUpload.recommendationEngine.database.entity.*;
import com.musicUpload.musicUpload.recommendationEngine.database.repository.*;
import com.musicUpload.musicUpload.recommendationEngine.dto.StartEndDateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.musicUpload.musicUpload.recommendationEngine.exceptions.UnauthenticatedException;
import com.musicUpload.musicUpload.recommendationEngine.exceptions.NotFoundException;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendationEngine {
    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final AlbumRepository albumRepository;
    private final UserSongCustomRepository userSongCustomRepository;
    private final Map<Integer, Double> monthToMultiplierMap = Map.ofEntries(
            Map.entry(1, 1.0),
            Map.entry(2, 0.8),
            Map.entry(3, 0.6),
            Map.entry(4, 0.4),
            Map.entry(5, 0.2),
            Map.entry(6, 0.1)
    );

    public List<Long> createRecommendationsForSong(Long songId, Long userId, Long pageSize, Long pageNumber) {
        if (!userSongCustomRepository.findRestrictedSongs(userId, List.of(songId)).isEmpty()) {
            return defaultRecommendations(pageSize, pageNumber);
        }

        List<Long> songTags = userSongCustomRepository.getSongIdToTagIdMap(List.of(songId));

        List<Long> sortedSongsIds = userSongCustomRepository.findSongsGroupedWithMonthlyListens(
                songTags, userId, List.of(songId), getDateMap(),
                monthToMultiplierMap, pageSize, pageNumber
        );

        if (sortedSongsIds.size() < pageSize) {
            return Stream.concat(sortedSongsIds.stream(),
                    defaultRecommendations(
                            pageSize, pageNumber, Stream.concat(sortedSongsIds.stream(), Stream.of(songId)).toList())
                            .stream()).toList();
        }

        return sortedSongsIds;
    }

    public List<Long> createRecommendationsForUser(Long userId, Long pageSize, Long pageNumber) {
        if (userId.equals(0L)) {
            return defaultRecommendations(pageSize, pageNumber);
        }

        userRepository.findById(userId).orElseThrow(UnauthenticatedException::new);

        List<Long> songs = userSongCustomRepository.findSongIdsByUserIdAndCreatedAtBetween(
                userId, getDateMonthsFromToday(3), new Date()
        );

        List<Long> restrictedSongs = userSongCustomRepository.findRestrictedSongs(userId, songs);

        List<Long> songTags = userSongCustomRepository.getSongIdToTagIdMap(songs);

        Date startDate = getDateMonthsFromToday(1);

        List<Long> sortedSongIds = userSongCustomRepository.findSongsForGivenUser(
                songs, songTags, userId, restrictedSongs, pageSize, pageNumber, startDate);

        return fallbackToDefaultRecommendations(sortedSongIds, pageSize, pageNumber);
    }

    public List<Long> createRecommendationsForAlbum(Long albumId, Long userId, Long pageSize, Long pageNumber) {
        Album a = albumRepository.findById(albumId)
                .orElseThrow(NotFoundException::new);

        List<Long> songs = a.getSongs().stream().map(Song::getId).toList();
        Set<Long> restrictedSongs = new HashSet<>(userSongCustomRepository.findRestrictedSongs(userId, songs));
        songs = songs.stream().filter(s -> !restrictedSongs.contains(s)).toList();
        List<Long> songTags = userSongCustomRepository.getSongIdToTagIdMap(songs);

        List<Long> sortedSongIds = userSongCustomRepository.findSongsGroupedWithMonthlyListens(
                songTags, userId, songs, getDateMap(),
                monthToMultiplierMap, pageSize, pageNumber);

        return fallbackToDefaultRecommendations(sortedSongIds, pageSize, pageNumber);
    }

    private List<Long> fallbackToDefaultRecommendations(List<Long> sortedSongIds, Long pageSize, Long pageNumber) {
        if (sortedSongIds.size() < pageSize) {
            return Stream.concat(sortedSongIds.stream(), defaultRecommendations(pageSize, pageNumber, sortedSongIds).stream()).toList();
        }
        return sortedSongIds;
    }


    private List<Long> defaultRecommendations(Long pageSize, Long pageNumber, List<Long> excludedSongIds) {
        return excludedSongIds.isEmpty() ?
                songRepository.findByProtectionTypeOrderByListenCount(PageRequest.of(pageNumber.intValue(), pageSize.intValue())).getContent()
                : songRepository.findByProtectionTypeOrderByListenCount(excludedSongIds, PageRequest.of(pageNumber.intValue(), pageSize.intValue())).getContent();
    }

    private List<Long> defaultRecommendations(Long pageSize, Long pageNumber) {
        return songRepository.findByProtectionTypeOrderByListenCount(PageRequest.of(pageNumber.intValue(), pageSize.intValue())).getContent();
    }

    private Map<Integer, StartEndDateDto> getDateMap() {
        return IntStream.range(1, 7)
                .mapToObj(i ->
                        Map.entry(i, StartEndDateDto.builder()
                                .startDate(getDateMonthsFromToday(i))
                                .endDate(i == 1 ? new Date() : getDateMonthsFromToday(i - 1))
                                .build())
                ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Date getDateMonthsFromToday(int monthsAgo) {
        return Date.from(
                LocalDate.now()
                        .minusMonths(monthsAgo)
                        .atStartOfDay()
                        .toInstant(ZoneOffset.UTC)
        );
    }
}
