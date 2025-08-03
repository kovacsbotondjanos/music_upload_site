package com.musicUpload.musicUpload.recommendationEngine.database.repository;

import com.musicUpload.musicUpload.recommendationEngine.database.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);
}
