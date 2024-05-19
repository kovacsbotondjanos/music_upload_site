package com.musicUpload.endpoints.userInterface;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class AlbumController {

    @GetMapping("/albums")
    public String getAlbums(){

        return "error";
    }

    @GetMapping("/albums/{id}")
    public String getAlbum(@PathVariable Long id){

        return "error";
    }

    @PostMapping("/albums/create")
    public String createAlbum(){

        return "error";
    }

    @PatchMapping("/albums/{id}")
    public String patchAlbum(@PathVariable Long id){

        return "error";
    }

    @DeleteMapping("/albums/{id}")
    public String deleteAlbum(@PathVariable Long id){

        return "error";
    }
}
