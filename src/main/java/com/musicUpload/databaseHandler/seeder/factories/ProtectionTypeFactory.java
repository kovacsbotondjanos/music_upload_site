package com.musicUpload.databaseHandler.seeder.factories;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.musicUpload.databaseHandler.models.ProtectionType;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProtectionTypeFactory {
    public List<ProtectionType> generateProtectionTypes(){
        List<ProtectionType> protectionTypes;

        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream("protectionConfig.json");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            TypeToken<List<ProtectionType>> token = new TypeToken<>() {};
            protectionTypes = gson.fromJson(reader, token.getType());

            return protectionTypes;
        }
        catch (Exception e){
            System.err.println(e.getMessage());
        }

        return new ArrayList<>();
    }
}
