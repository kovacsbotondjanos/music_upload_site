package com.musicUpload.musicUpload.recommendationEngine.database.repository;

import com.musicUpload.musicUpload.recommendationEngine.database.entity.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SongRepository extends JpaRepository<Song, Long> {

    @Query(value = """
            SELECT id
            FROM song
            WHERE protection_type = 'PUBLIC'
            AND id not in (:songIds) AND listen_count != 0
            ORDER BY listen_count DESC
            """,
            countQuery = "SELECT COUNT(*) FROM song WHERE protection_type = 'PUBLIC' AND listen_count != 0 AND id not in (:songIds)",
            nativeQuery = true)
    Page<Long> findByProtectionTypeOrderByListenCount(
            @Param("songIds") List<Long> songIds,
            Pageable pageable
    );

    @Query(value = """
            SELECT id
            FROM song
            WHERE protection_type = 'PUBLIC'
            AND listen_count != 0
            ORDER BY listen_count DESC
            """,
            countQuery = "SELECT COUNT(*) FROM song WHERE protection_type = 'PUBLIC' AND listen_count != 0",
            nativeQuery = true)
    Page<Long> findByProtectionTypeOrderByListenCount(Pageable pageable);
}
