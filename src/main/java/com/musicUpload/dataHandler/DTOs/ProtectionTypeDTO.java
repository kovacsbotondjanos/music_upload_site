package com.musicUpload.dataHandler.DTOs;

import com.musicUpload.dataHandler.models.ProtectionType;
import lombok.Data;

@Data
public class ProtectionTypeDTO {
    private Long id;
    private String name;

    public ProtectionTypeDTO(ProtectionType protectionType){
        this.id = protectionType.getId();
        this.name = protectionType.getName();
    }
}
