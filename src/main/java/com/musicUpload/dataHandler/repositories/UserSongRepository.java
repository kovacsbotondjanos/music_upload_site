package com.musicUpload.dataHandler.repositories;

import com.musicUpload.dataHandler.models.implementations.UserSong;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;
import java.util.Set;

public interface UserSongRepository extends JpaRepository<UserSong, Long> {
    Optional<UserSong> findByCreatedAtBetween(Date start, Date end);
    Set<UserSong> findByCreatedAtGreaterThan(Date start);
}
