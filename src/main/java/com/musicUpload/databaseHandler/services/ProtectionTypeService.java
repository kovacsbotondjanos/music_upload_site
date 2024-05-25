package com.musicUpload.databaseHandler.services;

import com.musicUpload.databaseHandler.models.ProtectionType;
import com.musicUpload.databaseHandler.repositories.ProtectionTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<ProtectionType> getAllPossibleProtectionType(){
        return protectionTypeRepository.findAll();
    }
}
