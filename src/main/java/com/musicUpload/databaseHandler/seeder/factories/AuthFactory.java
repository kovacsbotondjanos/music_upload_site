package com.musicUpload.databaseHandler.seeder.factories;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.musicUpload.databaseHandler.models.auth.Auth;
import com.musicUpload.databaseHandler.models.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuthFactory {
    public List<Auth> createAuthorities(){
        List<Auth> auths;

        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream("authConfig.json");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            TypeToken<List<Auth>> token = new TypeToken<>() {};
            auths = gson.fromJson(reader, token.getType());

            return auths;
        }
        catch (Exception e){
            System.err.println(e.getMessage());
        }

        return new ArrayList<>();
    }
}
