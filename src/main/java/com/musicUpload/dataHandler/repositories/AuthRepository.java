package com.musicUpload.dataHandler.repositories;

import com.musicUpload.dataHandler.models.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<Auth, Long> {
}
