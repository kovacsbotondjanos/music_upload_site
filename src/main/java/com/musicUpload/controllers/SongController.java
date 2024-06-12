package com.musicUpload.controllers;

import com.musicUpload.dataHandler.DTOs.SongDTO;
import com.musicUpload.dataHandler.details.CustomUserDetails;
import com.musicUpload.dataHandler.services.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1/songs")
@CrossOrigin
public class SongController {
    private final SongService songService;

    @Autowired
    public SongController(SongService songService) {
        this.songService = songService;
    }

    @GetMapping
    public List<SongDTO> getSongs(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return songService.getSongs(userDetails);
    }

    @GetMapping("/{id}")
    public SongDTO getSongById(@AuthenticationPrincipal CustomUserDetails userDetails,
                               @PathVariable Long id) {
        return songService.findById(userDetails, id);
    }

    @GetMapping("/search/{name}")
    public List<SongDTO> getSongByNameLike(@AuthenticationPrincipal CustomUserDetails userDetails,
                                           @PathVariable String name) {
        return songService.findByNameLike(userDetails, name);
    }

    @GetMapping("/random")
    public List<SongDTO> getRandomSongs(@AuthenticationPrincipal CustomUserDetails userDetails,
                                        @RequestParam(name = "pageNumber", defaultValue = "0", required = false) int pageNumber,
                                        @RequestParam(name = "pageSize", defaultValue = "10", required = false) int pageSize) {
        return songService.getRecommendedSongs(userDetails, pageNumber, pageSize);
    }

    @PostMapping("/add")
    @ResponseStatus(value = HttpStatus.CREATED)
    public void createSong(@AuthenticationPrincipal CustomUserDetails userDetails,
                           @RequestParam(name = "protection_type") String protectionType,
                           @RequestParam(name = "name") String name,
                           @RequestParam(name = "image", required = false) MultipartFile image,
                           @RequestParam(name = "song") MultipartFile song) {

        songService.addSong(
                userDetails,
                protectionType,
                name,
                image,
                song
        );
    }

    @PatchMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void patchSong(@AuthenticationPrincipal CustomUserDetails userDetails,
                          @PathVariable Long id,
                          @RequestParam(name = "protection_type", required = false) String protectionType,
                          @RequestParam(name = "name", required = false) String name,
                          @RequestParam(name = "image", required = false) MultipartFile image) {
        songService.patchSong(
                userDetails,
                id,
                protectionType,
                name,
                image
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteSong(@AuthenticationPrincipal CustomUserDetails userDetails,
                           @PathVariable Long id) {
        songService.deleteSong(userDetails, id);
    }
}
