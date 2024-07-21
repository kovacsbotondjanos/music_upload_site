package com.musicUpload.dataHandler.repositories;

import com.musicUpload.dataHandler.enums.ProtectionType;
import com.musicUpload.dataHandler.models.implementations.Song;
import com.musicUpload.dataHandler.models.implementations.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SongRepository extends JpaRepository<Song, Long> {
    List<Song> findByProtectionTypeOrderByListenCountDesc(ProtectionType protectionType, Pageable page);

    @Query(value = "SELECT * " +
            "FROM songs " +
            "WHERE name LIKE CONCAT('%', :name, '%') " +
            "ORDER BY " +
            "CASE " +
            "WHEN name LIKE CONCAT(:name, '%') THEN 1 " +
            "ELSE 2 " +
            "END, " +
            "name", nativeQuery = true)
    List<Song> findByNameLike(@Param("name") String name);

    Optional<Song> findByNameHashed(String name);

    List<Song> findByUser(User user);
}
