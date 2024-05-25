package com.musicUpload.databaseHandler.repositories;

import com.musicUpload.databaseHandler.models.ProtectionType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProtectionTypeRepository extends JpaRepository<ProtectionType, Long> {
}
