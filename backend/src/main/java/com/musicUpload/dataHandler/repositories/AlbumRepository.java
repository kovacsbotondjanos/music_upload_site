package com.musicUpload.dataHandler.repositories;

import com.musicUpload.dataHandler.enums.ProtectionType;
import com.musicUpload.dataHandler.models.implementations.Album;
import com.musicUpload.dataHandler.models.implementations.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    @Query(value = "SELECT * " +
            "FROM albums " +
            "WHERE name LIKE CONCAT('%', :name, '%') " +
            "AND (user = :userId OR protection_type = :protection_type)" +
            "ORDER BY " +
            "CASE " +
            "WHEN name LIKE CONCAT(:name, '%') THEN 1 " +
            "ELSE 2 " +
            "END, " +
            "name", nativeQuery = true)
    List<Album> findByNameLike(
            @Param("name") String name,
            @Param("userId") Long id,
            @Param("protection_type") ProtectionType protectionType,
            Pageable pageable
    );

    @Query(value = "SELECT * " +
            "FROM albums " +
            "WHERE id IN :ids " +
            "AND (user = :userId " +
            "OR protection_type = :protection_type)", nativeQuery = true)
    List<Album> findByIdInAndUserOrIdInAndProtectionType(
            @Param("ids") List<Long> ids,
            @Param("userId") Long userId,
            @Param("protection_type") ProtectionType protectionType
            );

    List<Album> findByUser(User user);

    Optional<Album> findByUserAndId(User user, Long id);
}
