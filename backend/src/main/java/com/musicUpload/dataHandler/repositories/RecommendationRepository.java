package com.musicUpload.dataHandler.repositories;

import com.musicUpload.dataHandler.models.implementations.Recommendation;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    @Query(value = "SELECT * FROM " +
            "recommendation WHERE " +
            "user_id = :userId",
            nativeQuery = true)
    List<Recommendation> findByUser(
            @Param("userId") Long userId
    );

    @Query(value = "SELECT created_at FROM " +
            "recommendation WHERE " +
            "user_id = :userId LIMIT 1",
            nativeQuery = true)
    Optional<Date> findLastDate(@Param("userId") Long userId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM " +
            "recommendation WHERE " +
            "user_id = :userId",
            nativeQuery = true)
    void deleteByUser(@Param("userId") Long userId);
}
