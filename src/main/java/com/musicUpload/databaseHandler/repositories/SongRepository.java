package com.musicUpload.databaseHandler.repositories;

import com.musicUpload.databaseHandler.models.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SongRepository extends JpaRepository<Song, Long> {
    @Query(value = "SELECT s.id AS id, s.image AS image, s.name AS name, s.name_hashed AS name_hashed, s.protection_id AS protection_id, s.user_id AS user_id, s.created_at AS created_at, s.updated_at AS updated_at FROM songs s INNER JOIN protection_type pt ON s.protection_id = pt.id WHERE pt.name = 'PUBLIC' ORDER BY RAND() LIMIT 10",
            nativeQuery = true)
    List<Song> findRandomSongs();
    Optional<Song> findByNameHashed(String name);
}
