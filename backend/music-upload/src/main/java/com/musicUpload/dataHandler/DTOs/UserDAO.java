package com.musicUpload.dataHandler.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDAO {
    private String username;
    private String email;
    private String password;
    private String oldPassword;
}
