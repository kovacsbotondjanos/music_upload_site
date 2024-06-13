package com.musicUpload.dataHandler.repositories;

import com.musicUpload.dataHandler.models.implementations.UserRecommendation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRecommendationRepository extends JpaRepository<UserRecommendation, Long> {
    List<UserRecommendation> findByUserIdOrderByRecommendationPosition(Long id, Pageable pageable);
}
