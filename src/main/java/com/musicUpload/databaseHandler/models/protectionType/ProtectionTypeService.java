package com.musicUpload.databaseHandler.models.protectionType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProtectionTypeService {
    @Autowired
    private final ProtectionTypeRepository protectionTypeRepository;

    public ProtectionTypeService(ProtectionTypeRepository protectionTypeRepository) {
        this.protectionTypeRepository = protectionTypeRepository;
    }

    public ProtectionType save(ProtectionType protectionType){
        return protectionTypeRepository.save(protectionType);
    }
}
