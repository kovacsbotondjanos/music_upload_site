package com.musicUpload.controllers;

import com.musicUpload.dataHandler.DTOs.AlbumCreateAndPatchDTO;
import com.musicUpload.dataHandler.DTOs.AlbumDTO;
import com.musicUpload.dataHandler.details.UserDetailsImpl;
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
    public List<AlbumDTO> getAlbums(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return albumService.getAlbums(userDetails);
    }

    @GetMapping("/{id}")
    public AlbumDTO getAlbum(@AuthenticationPrincipal UserDetailsImpl userDetails,
                             @PathVariable Long id) {
        return albumService.findById(id, userDetails);
    }

    @GetMapping("/search/{name}")
    public List<AlbumDTO> getAlbumByNameLike(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                             @PathVariable String name) {
        return albumService.findByNameLike(userDetails, name);
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public void createAlbum(@AuthenticationPrincipal UserDetailsImpl userDetails,
                            @ModelAttribute AlbumCreateAndPatchDTO andCreateDTO,
                            @RequestParam(name = "image", required = false) MultipartFile image) {
        albumService.saveAlbum(
                userDetails,
                andCreateDTO.getProtectionType(),
                andCreateDTO.getName(),
                image);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public AlbumDTO patchAlbum(@AuthenticationPrincipal UserDetailsImpl userDetails,
                               @PathVariable Long id,
                               @ModelAttribute AlbumCreateAndPatchDTO albumPatchDTO,
                               @RequestParam(name = "image", required = false) MultipartFile image) {
        return AlbumDTO.of(albumService.patchAlbum(
                userDetails,
                id,
                albumPatchDTO.getProtectionType(),
                albumPatchDTO.getSongIds(),
                albumPatchDTO.getName(),
                image
        ));
    }

    @PatchMapping("/add-songs/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public AlbumDTO addSongs(@AuthenticationPrincipal UserDetailsImpl userDetails,
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
    public void deleteAlbum(@AuthenticationPrincipal UserDetailsImpl userDetails,
                            @PathVariable Long id) {
        albumService.deleteAlbum(userDetails, id);
    }
}
