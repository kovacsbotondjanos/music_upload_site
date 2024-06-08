package com.musicUpload.dataHandler.seeder.factories;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.musicUpload.dataHandler.models.implementations.Auth;
import com.musicUpload.dataHandler.services.AuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuthFactory {
    private static final Logger logger = LogManager.getLogger(ProtectionTypeFactory.class);
    private final AuthService authService;

    @Autowired
    public AuthFactory(AuthService authService) {
        this.authService = authService;
    }

    public List<Auth> createAuthorities() {
        List<Auth> auths;

        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream("authConfig.json")) {
            assert inputStream != null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                TypeToken<List<Auth>> token = new TypeToken<>() { };
                auths = gson.fromJson(reader, token.getType());
                auths.stream().parallel().forEach(authService::save);
                return auths;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return new ArrayList<>();
    }
}
