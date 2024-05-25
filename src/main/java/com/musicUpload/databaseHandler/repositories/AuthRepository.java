package com.musicUpload.databaseHandler.repositories;

import com.musicUpload.databaseHandler.models.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<Auth, Long> {
}
