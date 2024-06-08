package com.musicUpload.dataHandler.repositories;

import com.musicUpload.dataHandler.models.implementations.UserSong;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSongRepository extends JpaRepository<UserSong, Long> {
    Optional<UserSong> findBySongIdAndUserIdAndYearAndMonth(Long userId, Long songId, int year, int month);
}
