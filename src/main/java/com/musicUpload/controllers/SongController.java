package com.musicUpload.controllers;

import com.musicUpload.dataHandler.DTOs.SongDTO;
import com.musicUpload.dataHandler.services.AlbumService;
import com.musicUpload.dataHandler.models.Song;
import com.musicUpload.dataHandler.services.SongService;
import com.musicUpload.dataHandler.details.CustomUserDetails;
import com.musicUpload.dataHandler.services.UserService;
import com.musicUpload.util.ImageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/songs")
@CrossOrigin
public class SongController {
    private final SongService songService;
    private final AlbumService albumService;
    private final UserService userService;
    private final ImageFactory imageFactory;

    @Autowired
    public SongController(SongService songService, AlbumService albumService, UserService userService, ImageFactory imageFactory) {
        this.songService = songService;
        this.albumService = albumService;
        this.userService = userService;
        this.imageFactory = imageFactory;
    }

    @GetMapping
    public List<SongDTO> getSongs(@AuthenticationPrincipal CustomUserDetails userDetails){
        return songService.getSongs(userDetails);
    }

    @GetMapping("/{id}")
    public SongDTO getSongById(@AuthenticationPrincipal CustomUserDetails userDetails,
                                         @PathVariable Long id){
        return songService.findById(userDetails, id);
    }

    @GetMapping("/random")
    public List<SongDTO> getRandomSongs(){
        return songService.getRandomSongs();
    }

    @PostMapping("/create")
    public ResponseEntity<String> createSong(@AuthenticationPrincipal CustomUserDetails userDetails,
                                        @RequestParam(name = "protection_type") String protectionType,
                                        @RequestParam(name = "name") String name,
                                        @RequestParam(name = "image", required = false) MultipartFile image,
                                        @RequestParam(name = "song") MultipartFile song){
        //TODO: make these parallel too
        new Thread(() -> userDetails.addSong(songService.saveSong(
            userDetails,
            protectionType,
            name,
            image,
            song,
            userDetails.getId()
        ))).start();
        return ResponseEntity.ok("song created successfully");
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> patchSong(@AuthenticationPrincipal CustomUserDetails userDetails,
                                       @PathVariable Long id,
                                       @RequestParam(name = "protection_type", required = false) String protectionType,
                                       @RequestParam(name = "name", required = false) String name,
                                       @RequestParam(name = "image", required = false) MultipartFile image){
        new Thread(() -> songService.updateSong(
            userDetails,
            id,
            protectionType,
            name,
            image
        )).start();
        return ResponseEntity.ok("song edited successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSong(@AuthenticationPrincipal CustomUserDetails userDetails,
                                             @PathVariable Long id){

        new Thread(() -> {
            Song song = songService.deleteSong(userDetails, id);
            userDetails.getSongs().remove(song);
        }).start();
        return ResponseEntity.ok("song deleted successfully");
    }
}
