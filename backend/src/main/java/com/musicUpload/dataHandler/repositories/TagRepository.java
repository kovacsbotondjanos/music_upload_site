package com.musicUpload.dataHandler.repositories;

import com.musicUpload.dataHandler.models.implementations.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    @Query(value = "SELECT * " +
            "FROM tag " +
            "WHERE name LIKE CONCAT('%', :name, '%') " +
            "ORDER BY " +
            "CASE " +
            "WHEN name LIKE CONCAT(:name, '%') THEN 1 " +
            "ELSE 2 " +
            "END, " +
            "name",
            nativeQuery = true)
    List<Tag> findByNameLike(
            @Param("name") String name
    );

    Optional<Tag> findByName(String name);
}
