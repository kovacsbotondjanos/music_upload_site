package com.musicUpload.dataHandler.services;

import com.musicUpload.dataHandler.models.implementations.ProtectionType;
import com.musicUpload.dataHandler.repositories.ProtectionTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProtectionTypeService {
    private final ProtectionTypeRepository protectionTypeRepository;

    @Autowired
    public ProtectionTypeService(ProtectionTypeRepository protectionTypeRepository) {
        this.protectionTypeRepository = protectionTypeRepository;
    }

    public ProtectionType save(ProtectionType protectionType) {
        return protectionTypeRepository.save(protectionType);
    }

    public Optional<ProtectionType> getProtectionTypeByName(String name) {
        return protectionTypeRepository.findByName(name);
    }

    public List<ProtectionType> getAllPossibleProtectionType() {
        return protectionTypeRepository.findAll();
    }
}
