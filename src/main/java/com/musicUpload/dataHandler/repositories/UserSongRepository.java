package com.musicUpload.dataHandler.repositories;

import com.musicUpload.dataHandler.models.implementations.Song;
import com.musicUpload.dataHandler.models.implementations.User;
import com.musicUpload.dataHandler.models.implementations.UserSong;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSongRepository extends JpaRepository<UserSong, Long> {
    Optional<UserSong> findByUserAndSong(User user, Song song);
}
