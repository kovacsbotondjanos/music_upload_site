package com.musicUpload.dataHandler.repositories;

import com.musicUpload.dataHandler.models.Album;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    Optional<Album> findById(Long id);
}
