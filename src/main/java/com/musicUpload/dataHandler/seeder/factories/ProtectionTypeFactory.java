package com.musicUpload.dataHandler.seeder.factories;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.musicUpload.dataHandler.models.implementations.ProtectionType;
import com.musicUpload.dataHandler.services.ProtectionTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProtectionTypeFactory {
    private final ProtectionTypeService protectionTypeService;

    @Autowired
    public ProtectionTypeFactory(ProtectionTypeService protectionTypeService) {
        this.protectionTypeService = protectionTypeService;
    }

    public List<ProtectionType> generateProtectionTypes(){
        List<ProtectionType> protectionTypes;

        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream("protectionConfig.json")) {
            assert inputStream != null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                TypeToken<List<ProtectionType>> token = new TypeToken<>() {};
                protectionTypes = gson.fromJson(reader, token.getType());
                protectionTypes.stream().parallel().forEach(protectionTypeService::save);
                return protectionTypes;
            }
        }
        catch (Exception e){
            System.err.println(e.getMessage());
        }

        return new ArrayList<>();
    }
}
