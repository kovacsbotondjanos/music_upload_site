package com.musicUpload.musicUpload.recommendationEngine.database.repository;

import com.musicUpload.musicUpload.recommendationEngine.dto.StartEndDateDto;

import java.util.*;

public interface UserSongCustomRepository {

    List<Long> findRestrictedSongs(Long userId, List<Long> songIds);

    List<Long> findSongsGroupedWithMonthlyListens(
            Collection<Long> tagIds, Long userId, List<Long> songId, List<Long> userIds,
            Map<Integer, StartEndDateDto> startEndDateDtoMap, Map<Integer, Double> weightMap, Long pageSize, Long pageNumber
    );

    List<Long> getSongIdToTagIdMap(List<Long> songIds);

    List<Long> getUserIdsForSongs(List<Long> songIds, Date startDate);

    List<Long> findSongIdsByUserIdAndCreatedAtBetween(Long id, Date startDate, Date endDate);

    List<Long> findSongsForGivenUser(
            Collection<Long> songIds, Collection<Long> tagIds, Long userId, List<Long> userIds,
            List<Long> restrictedSongIds, Long pageSize, Long pageNumber, Date startDate
    );
}
