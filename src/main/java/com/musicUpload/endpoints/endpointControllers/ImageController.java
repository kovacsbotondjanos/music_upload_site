package com.musicUpload.endpoints.endpointControllers;


import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class ImageController {
    private final String pathName = "images\\";
    @GetMapping("/images/{name}")
    public ResponseEntity<Resource> getImage(@PathVariable String name){
        try{
            Path imagePath = Paths.get(pathName).resolve(name);
            Resource resource = new UrlResource(imagePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok().body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException me) {
            return ResponseEntity.notFound().build();
        }
    }
}
