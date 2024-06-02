package com.musicUpload.dataHandler.repositories;

import com.musicUpload.dataHandler.models.implementations.ProtectionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProtectionTypeRepository extends JpaRepository<ProtectionType, Long> {
    Optional<ProtectionType> findByName(String name);
}
