package com.musicUpload.dataHandler.services;

import com.musicUpload.dataHandler.models.Auth;
import com.musicUpload.dataHandler.repositories.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Optional<Auth> getByName(String name){
        return authRepository.findByName(name);
    }
}
