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
    @Query(value = "SELECT * " +
            "FROM song " +
            "WHERE name LIKE CONCAT('%', :name, '%') " +
            "AND (user_id = :userId OR protection_type = :protection_type)" +
            "ORDER BY " +
            "CASE " +
            "WHEN name LIKE CONCAT(:name, '%') THEN 1 " +
            "ELSE 2 " +
            "END, " +
            "name",
            countQuery = "SELECT COUNT(*) " +
                    "FROM song " +
                    "WHERE name LIKE CONCAT('%', :name, '%') " +
                    "AND (user_id = :userId OR protection_type = :protection_type)",
            nativeQuery = true)
    List<Song> findByNameLike(
            @Param("name") String name,
            @Param("userId") Long id,
            @Param("protection_type") ProtectionType protectionType,
            Pageable pageable
    );

    @Query(value = "SELECT * " +
            "FROM song " +
            "WHERE id IN :ids " +
            "AND (user_id = :userId " +
            "OR protection_type = :protection_type)", nativeQuery = true)
    List<Song> findByIdInAndUserOrIdInAndProtectionType(
            @Param("ids") List<Long> ids,
            @Param("userId") Long userId,
            @Param("protection_type") ProtectionType protectionType
    );

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

    Optional<Song> findByNameHashed(String name);

    List<Song> findByUser(User user);

    Optional<Song> findByUserAndId(User user, Long id);
}
