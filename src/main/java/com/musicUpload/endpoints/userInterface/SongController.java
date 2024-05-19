package com.musicUpload.endpoints.userInterface;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class SongController {
    @GetMapping("/songs")
    public String getSongs(){

        return "error";
    }

    @GetMapping("/songs/{id}")
    public String getSong(@PathVariable Long id){

        return "error";
    }

    @PostMapping("/songs/create")
    public String createSong(){

        return "error";
    }

    @PatchMapping("/songs/{id}")
    public String patchSong(@PathVariable Long id){

        return "error";
    }

    @DeleteMapping("/songs/{id}")
    public String deleteSong(@PathVariable Long id){

        return "error";
    }
}
