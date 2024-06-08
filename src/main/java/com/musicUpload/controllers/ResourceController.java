package com.musicUpload.controllers;

import com.musicUpload.dataHandler.details.CustomUserDetails;
import com.musicUpload.dataHandler.services.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@CrossOrigin
public class ResourceController {
    private final String imagePathName = "images" + FileSystems.getDefault().getSeparator();
    private final SongService songService;

    @Autowired
    public ResourceController(SongService songService) {
        this.songService = songService;
    }

    //TODO: put the business logic in an other calss
    @GetMapping("/images/{name}")
    public ResponseEntity<Resource> getImage(@PathVariable String name) {
        try {
            Path imagePath = Paths.get(imagePathName).resolve(name);
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

    @GetMapping("/music/{nameHashed}")
    public ResponseEntity<Resource> getSong(@AuthenticationPrincipal CustomUserDetails userDetails,
                                            @PathVariable String nameHashed) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .body(songService.getSongInResourceFormatByNameHashed(userDetails, nameHashed));
    }
}
