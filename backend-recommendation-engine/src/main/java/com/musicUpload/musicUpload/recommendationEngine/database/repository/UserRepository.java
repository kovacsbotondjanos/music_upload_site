package com.musicUpload.musicUpload.recommendationEngine.database.repository;

import com.musicUpload.musicUpload.recommendationEngine.database.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
