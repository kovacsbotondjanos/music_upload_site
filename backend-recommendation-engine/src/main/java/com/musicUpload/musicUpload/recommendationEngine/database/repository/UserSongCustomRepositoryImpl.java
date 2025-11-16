package com.musicUpload.musicUpload.recommendationEngine.database.repository;

import com.musicUpload.musicUpload.recommendationEngine.dto.StartEndDateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserSongCustomRepositoryImpl implements UserSongCustomRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final String SONG_NOT_EXISTS = """
            SELECT id AS songId
            FROM song
            WHERE id IN (:songIds)
              AND NOT (user_id = :userId OR protection_type IN ('PUBLIC', 'PROTECTED'))
            """;

    private final String FIND_BY_CREATED_AT_BETWEEN = """
            SELECT
                us.song_id AS songId,
                (
                    (SUM(CASE WHEN us.created_at BETWEEN :start1 AND NOW() THEN 1 ELSE 0 END) * :weight1) +
                    (SUM(CASE WHEN us.created_at BETWEEN :start2 AND :end2 THEN 1 ELSE 0 END) * :weight2) +
                    (SUM(CASE WHEN us.created_at BETWEEN :start3 AND :end3 THEN 1 ELSE 0 END) * :weight3) +
                    (SUM(CASE WHEN us.created_at BETWEEN :start4 AND :end4 THEN 1 ELSE 0 END) * :weight4) +
                    (SUM(CASE WHEN us.created_at BETWEEN :start5 AND :end5 THEN 1 ELSE 0 END) * :weight5) +
                    (SUM(CASE WHEN us.created_at BETWEEN :start6 AND :end6 THEN 1 ELSE 0 END) * :weight6)
                ) AS songIndex
            FROM user_song us
            INNER JOIN tag_song ts ON us.song_id = ts.song_id
            INNER JOIN song s ON us.song_id = s.id
            WHERE ts.tag_id IN (:tagIds)
            AND us.created_at >= :end6
            AND us.song_id NOT IN (:songIds)
            AND (:userId is NULL OR s.user_id <> :userId)
            AND protection_type = 'PUBLIC'
            GROUP BY us.song_id
            ORDER BY songIndex DESC
            LIMIT :limit OFFSET :offset
            """;

    private final String FIND_BY_CREATED_AT_BETWEEN_BATCH = """
            SELECT us.song_id as songId, COUNT(*) AS count
            FROM user_song us
            INNER JOIN tag_song ts ON us.song_id = ts.song_id
            INNER JOIN song s ON us.song_id = s.id
            WHERE us.created_at >= :startDate
            AND ts.tag_id IN (:tagIds)
            AND (s.user_id = :userId OR protection_type = 'PUBLIC')
            AND (us.user_id <> :userId OR :userId IS NULL)
            AND (:restrictedSongsEmpty OR us.song_id NOT IN (:restrictedSongIds))
            GROUP BY us.song_id
            ORDER BY count DESC
            LIMIT :limit OFFSET :offset
            """;

    private final String CREATE_SONG_TAG_MAP = """
            SELECT DISTINCT tag_id AS tagId FROM tag_song WHERE song_id IN (:songIds)
            """;

    private final String GET_SONG_IDS_FOR_USER = """
            SELECT song_id AS songId FROM user_song
            WHERE user_id = :userId AND created_at BETWEEN :startDate AND :endDate
            """;

    private final String TAG_IDS = "tagIds";
    private final String SONG_IDS = "songIds";
    private final String USER_ID = "userId";
    private final String START_DATE = "startDate";
    private final String END_DATE = "endDate";
    private final String RESTRICTED_SONG_IDS = "restrictedSongIds";
    private final String RESTRICTED_SONGS_EMPTY = "restrictedSongsEmpty";
    private final String PAGE_SIZE = "limit";
    private final String OFFSET = "offset";
    private final Map<Integer, String> startParamMap = Map.of(
            1, "start1",
            2, "start2",
            3, "start3",
            4, "start4",
            5, "start5",
            6, "start6"
    );
    private final Map<Integer, String> endParamMap = Map.of(
            1, "end1",
            2, "end2",
            3, "end3",
            4, "end4",
            5, "end5",
            6, "end6"
    );
    private final Map<Integer, String> weightParamMap = Map.of(
            1, "weight1",
            2, "weight2",
            3, "weight3",
            4, "weight4",
            5, "weight5",
            6, "weight6"
    );

    @Override
    public List<Long> findRestrictedSongs(Long userId, List<Long> songIds) {
        if (songIds.isEmpty()) {
            return List.of();
        }

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(USER_ID, userId)
                .addValue(SONG_IDS, songIds);
        return namedParameterJdbcTemplate.query(SONG_NOT_EXISTS, params, (row, rs) -> row.getLong("songId"));
    }

    @Override
    public List<Long> findSongsGroupedWithMonthlyListens(
            Collection<Long> tagIds, Long userId, List<Long> songIds, Map<Integer, StartEndDateDto> startEndDateDtoMap,
            Map<Integer, Double> weightMap, Long pageSize, Long pageNumber) {

        if (tagIds.isEmpty()) {
            log.error("songs({}) have no tags", songIds);
            return List.of();
        }

        if (songIds.isEmpty()) {
            log.warn("No song IDs provided, returning empty list");
            return List.of();
        }

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(SONG_IDS, songIds)
                .addValue(TAG_IDS, tagIds)
                .addValue(USER_ID, userId)
                .addValue(PAGE_SIZE, pageSize)
                .addValue(OFFSET, pageNumber * pageSize);

        startEndDateDtoMap.forEach((i, startEndDateDto) -> {
            var startEndDate = startEndDateDtoMap.get(i);
            params.addValue(startParamMap.get(i), startEndDate.getStartDate());
            params.addValue(endParamMap.get(i), startEndDate.getEndDate());
            params.addValue(weightParamMap.get(i), weightMap.get(i));
        });

        return namedParameterJdbcTemplate.query(FIND_BY_CREATED_AT_BETWEEN, params, getLongRowMapperForSongId());
    }

    @Override
    public List<Long> findSongsForGivenUser(
            Collection<Long> songIds, Collection<Long> tagIds, Long userId, List<Long> restrictedSongIds, Long pageSize, Long pageNumber, Date startDate) {
        if (songIds.isEmpty()) {
            return List.of();
        }

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(SONG_IDS, songIds)
                .addValue(TAG_IDS, tagIds)
                .addValue(USER_ID, userId)
                .addValue(START_DATE, startDate)
                .addValue(RESTRICTED_SONGS_EMPTY, restrictedSongIds.isEmpty())
                .addValue(RESTRICTED_SONG_IDS, restrictedSongIds.isEmpty() ? null : restrictedSongIds)
                .addValue(OFFSET, pageNumber * pageSize)
                .addValue(PAGE_SIZE, pageSize);

        return namedParameterJdbcTemplate.query(FIND_BY_CREATED_AT_BETWEEN_BATCH, params, getLongRowMapperForSongId());
    }

    @Override
    public List<Long> getSongIdToTagIdMap(List<Long> songIds) {
        if (songIds.isEmpty()) {
            return List.of();
        }

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(SONG_IDS, songIds);
        return namedParameterJdbcTemplate.query(CREATE_SONG_TAG_MAP, params, getLongRowMapperForTagId());
    }

    @Override
    public List<Long> findSongIdsByUserIdAndCreatedAtBetween(Long id, Date startDate, Date endDate) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(USER_ID, id)
                .addValue(START_DATE, startDate)
                .addValue(END_DATE, endDate);
        return namedParameterJdbcTemplate.query(GET_SONG_IDS_FOR_USER, params, getLongRowMapperForSongId());
    }

    private RowMapper<Long> getLongRowMapperForSongId() {
        return (row, rs) -> row.getLong("songId");
    }

    private RowMapper<Long> getLongRowMapperForTagId() {
        return (row, rs) -> row.getLong("tagId");
    }
}
