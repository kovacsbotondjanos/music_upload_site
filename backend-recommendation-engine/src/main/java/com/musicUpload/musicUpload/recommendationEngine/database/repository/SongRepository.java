package com.musicUpload.musicUpload.recommendationEngine.database.repository;

import com.musicUpload.musicUpload.recommendationEngine.database.entity.ProtectionType;
import com.musicUpload.musicUpload.recommendationEngine.database.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SongRepository extends JpaRepository<Song, Long> {

    @Query(value = "SELECT * " +
            "FROM song " +
            "WHERE id = :id " +
            "AND (user_id = :userId " +
            "OR protection_type IN :protection_type)", nativeQuery = true)
    Optional<Song> findByIdAndProtectionTypeInOrUser(
            @Param("id") Long id,
            @Param("userId") Long userId,
            @Param("protection_type") List<ProtectionType> protectionType
    );

    @Query(value = "SELECT * " +
            "FROM song " +
            "WHERE protection_type = :protection_type " +
            "ORDER BY listen_count " +
            "LIMIT :limit", nativeQuery = true)
    List<Long> findByProtectionTypeOrderByListenCount(
            @Param("protection_type") ProtectionType protectionType,
            @Param("limit") Long limit
    );
}
