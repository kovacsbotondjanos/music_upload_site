package com.musicUpload.dataHandler.repositories;

import com.musicUpload.dataHandler.models.implementations.Album;
import com.musicUpload.dataHandler.models.implementations.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    @Query(value = "SELECT * " +
            "FROM albums " +
            "WHERE name LIKE CONCAT('%', :name, '%') " +
            "ORDER BY " +
            "CASE " +
            "WHEN name LIKE CONCAT(:name, '%') THEN 1 " +
            "ELSE 2 " +
            "END, " +
            "name", nativeQuery = true)
    List<Album> findByNameLike(@Param("name") String name);

    List<Album> findByUser(User user);

    Optional<Album> findByUserAndId(User user, Long id);
}
