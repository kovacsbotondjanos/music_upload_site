package com.musicUpload.dataHandler.repositories;

import com.musicUpload.dataHandler.models.implementations.UserSong;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

public interface UserSongRepository extends JpaRepository<UserSong, Long> {
    Optional<UserSong> findBySongIdAndUserIdAndCreatedAtBetween(Long songId, Long userId, Date start, Date end);

    Set<UserSong> findByCreatedAtGreaterThan(Date start);

    Set<UserSong> findBySongId(Long id);

    Set<UserSong> findByUserId(Long id);

    Set<UserSong> findByUserIdIn(Collection<Long> userIds);
}
