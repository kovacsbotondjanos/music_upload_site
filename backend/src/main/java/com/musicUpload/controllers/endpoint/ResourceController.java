package com.musicUpload.controllers.endpoint;

import com.musicUpload.dataHandler.services.MinioService;
import com.musicUpload.dataHandler.services.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/files")
@CrossOrigin
@RequiredArgsConstructor
public class ResourceController {
    private final SongService songService;
    private final MinioService minioService;

    @GetMapping("/music/{name}")
    public String getSong(@PathVariable String name) {
        return songService.getSong(name);
    }
}
