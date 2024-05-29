package com.musicUpload.dataHandler.DTOs;

import com.musicUpload.dataHandler.models.Auth;
import lombok.Data;

@Data
public class AuthDTO {
    private Long id;
    private String name;

    public AuthDTO(Auth auth){
        this.id = auth.getId();
        this.name = auth.getName();
    }

    public static AuthDTO of(Auth auth){
        return new AuthDTO(auth);
    }
}
