package com.musicUpload.databaseHandler.models.auth;

import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {
    @Autowired
    private final AuthRepository authRepository;

    public AuthService(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public Auth save(Auth auth){
        return authRepository.save(auth);
    }

    public List<Auth> getAllPossibleAuth(){
        return authRepository.findAll();
    }
}
