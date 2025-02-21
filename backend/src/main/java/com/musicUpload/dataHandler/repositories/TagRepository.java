package com.musicUpload.dataHandler.repositories;

import com.musicUpload.dataHandler.models.implementations.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TagRepository extends JpaRepository<Tag, Long> {
    @Query(value = "SELECT * " +
            "FROM TAG " +
            "WHERE name LIKE CONCAT('%', :name, '%') " +
            "ORDER BY " +
            "CASE " +
            "WHEN name LIKE CONCAT(:name, '%') THEN 1 " +
            "ELSE 2 " +
            "END, " +
            "name",
            countQuery = "SELECT COUNT(*) " +
                    "FROM TAG " +
                    "WHERE name LIKE CONCAT('%', :name, '%')",
            nativeQuery = true)
    List<Tag> findByNameLike(
            @Param("name") String name,
            Pageable pageable
    );

    @Query(value = "SELECT song_id " +
            "FROM TAG_SONG " +
            "WHERE tag_id in :tagIds",
            nativeQuery = true)
    Set<Long> findIdsForTagList(@Param("tagIds") List<Long> tagIds);

    Optional<Tag> findByNameIgnoreCase(String name);
}
