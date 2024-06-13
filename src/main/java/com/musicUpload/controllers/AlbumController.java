package com.musicUpload.controllers;

import com.musicUpload.dataHandler.DTOs.AlbumDTO;
import com.musicUpload.dataHandler.details.CustomUserDetails;
import com.musicUpload.dataHandler.services.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1/albums")
@CrossOrigin
public class AlbumController {
    private final AlbumService albumService;

    @Autowired
    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping
    public List<AlbumDTO> getAlbums(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return albumService.getAlbums(userDetails);
    }

    @GetMapping("/{id}")
    public AlbumDTO getAlbum(@AuthenticationPrincipal CustomUserDetails userDetails,
                             @PathVariable Long id) {
        return albumService.findById(id, userDetails);
    }

    @GetMapping("/search/{name}")
    public List<AlbumDTO> getAlbumByNameLike(@AuthenticationPrincipal CustomUserDetails userDetails,
                                             @PathVariable String name) {
        return albumService.findByNameLike(userDetails, name);
    }

    @PostMapping("/add")
    @ResponseStatus(value = HttpStatus.CREATED)
    public void createAlbum(@AuthenticationPrincipal CustomUserDetails userDetails,
                            @RequestParam(name = "protection_type") String protectionType,
                            @RequestParam(name = "name") String name,
                            @RequestParam(name = "image", required = false) MultipartFile image) {
        albumService.saveAlbum(
                userDetails,
                protectionType,
                name,
                image);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public AlbumDTO patchAlbum(@AuthenticationPrincipal CustomUserDetails userDetails,
                               @PathVariable Long id,
                               @RequestParam(name = "protection_type", required = false) String protectionType,
                               @RequestParam(name = "song_id", required = false) List<Long> songIds,
                               @RequestParam(name = "name", required = false) String name,
                               @RequestParam(name = "image", required = false) MultipartFile image) {
        return AlbumDTO.of(albumService.patchAlbum(
                userDetails,
                id,
                protectionType,
                songIds,
                name,
                image
        ));
    }

    @PatchMapping("/add-songs/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public AlbumDTO addSongs(@AuthenticationPrincipal CustomUserDetails userDetails,
                             @PathVariable Long id,
                             @RequestParam(name = "song_id", required = false) List<Long> songIds) {
        return AlbumDTO.of(albumService.addSongs(
                userDetails,
                id,
                songIds
        ));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteAlbum(@AuthenticationPrincipal CustomUserDetails userDetails,
                            @PathVariable Long id) {
        albumService.deleteAlbum(userDetails, id);
    }
}
