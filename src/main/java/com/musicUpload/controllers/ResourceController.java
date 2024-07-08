package com.musicUpload.controllers;

import com.musicUpload.dataHandler.details.UserDetailsImpl;
import com.musicUpload.dataHandler.services.MinioService;
import com.musicUpload.dataHandler.services.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/files")
@CrossOrigin
public class ResourceController {
    private final SongService songService;
    private final MinioService minioService;

    @Autowired
    public ResourceController(SongService songService,
                              MinioService minioService) {
        this.songService = songService;
        this.minioService = minioService;
    }

    @GetMapping("/image/{name}")
    public String getImage(@PathVariable String name) {
        return minioService.getImage(name);
    }

    @GetMapping("/music/{name}")
    public String getSong(@AuthenticationPrincipal UserDetailsImpl userDetails,
                          @PathVariable String name) {
        return songService.getSong(userDetails, name);
    }
}
