package com.musicUpload.controllers;

import com.musicUpload.dataHandler.DTOs.AlbumDTO;
import com.musicUpload.dataHandler.models.Album;
import com.musicUpload.dataHandler.services.AlbumService;
import com.musicUpload.dataHandler.services.SongService;
import com.musicUpload.dataHandler.details.CustomUserDetails;
import com.musicUpload.dataHandler.services.UserService;
import com.musicUpload.exceptions.UnauthenticatedException;
import com.musicUpload.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1/albums")
@CrossOrigin
public class AlbumController {
    private final SongService songService;
    private final AlbumService albumService;
    private final UserService userService;

    @Autowired
    public AlbumController(SongService songService, AlbumService albumService, UserService userService) {
        this.songService = songService;
        this.albumService = albumService;
        this.userService = userService;
    }

    @GetMapping
    public List<AlbumDTO> getAlbums(@AuthenticationPrincipal CustomUserDetails userDetails){
        return albumService.getAlbums(userDetails);
    }

    @GetMapping("/{id}")
    public AlbumDTO getAlbum(@AuthenticationPrincipal CustomUserDetails userDetails,
                             @PathVariable Long id){
        return albumService.findById(id, userDetails);
    }

    @PostMapping("/add")
    public ResponseEntity<String> createAlbum(@AuthenticationPrincipal CustomUserDetails userDetails,
                                              @RequestParam(name = "protection_type") String protectionType,
                                              @RequestParam(name = "name") String name,
                                              @RequestParam(name = "image", required = false) MultipartFile image){
        albumService.saveAlbum(
                     userDetails,
                     protectionType,
                     name,
                     image);
        return new ResponseEntity<>("successfully created", HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> patchAlbum(@AuthenticationPrincipal CustomUserDetails userDetails,
                                             @PathVariable Long id,
                                             @RequestParam(name = "protection_type", required = false) String protectionType,
                                             @RequestParam(name = "song_id", required = false) List<Long> songId,
                                             @RequestParam(name = "name", required = false) String name,
                                             @RequestParam(name = "image", required = false) MultipartFile image){
        //TODO: fix the issue and make this parallel too
        albumService.patchAlbum(
                userDetails,
                id,
                protectionType,
                songId,
                name,
                image
        );
        return ResponseEntity.ok("successfully edited album");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAlbum(@AuthenticationPrincipal CustomUserDetails userDetails,
                                         @PathVariable Long id){
        new Thread(() -> {
            Album album = albumService.deleteAlbum(userDetails, id);
            userDetails.getAlbums().remove(album);
        }).start();
        return new ResponseEntity<>(userDetails.getAlbums().stream().map(AlbumDTO::new).toList(), HttpStatus.NO_CONTENT);
    }
}
