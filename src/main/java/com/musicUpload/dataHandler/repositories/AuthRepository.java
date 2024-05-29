package com.musicUpload.dataHandler.repositories;

import com.musicUpload.dataHandler.models.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<Auth, Long> {
    Optional<Auth> findByName(String name);
}
