package com.musicUpload.dataHandler.repositories;

import com.musicUpload.dataHandler.models.Album;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlbumRepository extends JpaRepository<Album, Long> {
}
