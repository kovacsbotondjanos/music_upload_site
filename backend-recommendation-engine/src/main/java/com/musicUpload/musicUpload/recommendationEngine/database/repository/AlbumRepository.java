package com.musicUpload.musicUpload.recommendationEngine.database.repository;

import com.musicUpload.musicUpload.recommendationEngine.database.entity.Album;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlbumRepository extends JpaRepository<Album, Long> {
}
