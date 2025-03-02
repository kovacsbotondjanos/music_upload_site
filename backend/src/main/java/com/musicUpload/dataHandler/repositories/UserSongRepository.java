package com.musicUpload.dataHandler.repositories;

import com.musicUpload.dataHandler.models.implementations.UserSong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

public interface UserSongRepository extends JpaRepository<UserSong, Long> {
    Optional<UserSong> findBySongIdAndUserIdAndCreatedAtBetween(
            Long songId,
            Long userId,
            Date start,
            Date end
    );

    Set<UserSong> findByUserIdAndCreatedAtBetween(Long id, Date startDate, Date endDate);

    Set<UserSong> findBySongIdAndCreatedAtBetween(Long id, Date startDate, Date endDate);

    @Query(value = "SELECT * FROM user_song us " +
            "WHERE us.USER_ID in :ids " +
            "AND us.CREATED_AT BETWEEN :startDate AND :endDate " +
            "AND EXISTS ( " +
            "    SELECT 1 FROM tag_song ts " +
            "    WHERE ts.SONG_ID = us.SONG_ID " +
            "    AND ts.TAG_ID IN :tagIds " +
            ")", nativeQuery = true)
    Set<UserSong> findByUserIdInAndCreatedAtBetween(
            @Param("ids") Collection<Long> userIds,
            @Param("tagIds") Collection<Long> tagIds,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);
}
