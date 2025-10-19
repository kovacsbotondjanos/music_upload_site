package com.musicUpload.musicUpload.recommendationEngine.database.repository;

import com.musicUpload.musicUpload.recommendationEngine.database.entity.UserSong;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSongRepository extends JpaRepository<UserSong, Long> {
}
