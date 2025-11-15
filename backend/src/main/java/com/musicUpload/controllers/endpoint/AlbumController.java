package com.musicUpload.controllers.endpoint;

import com.musicUpload.dataHandler.DTOs.AlbumCardDTO;
import com.musicUpload.dataHandler.DTOs.AlbumDAO;
import com.musicUpload.dataHandler.DTOs.AlbumDTO;
import com.musicUpload.dataHandler.DTOs.SongDTO;
import com.musicUpload.dataHandler.services.AlbumService;
import com.musicUpload.dataHandler.services.UserRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1/albums")
@CrossOrigin
@RequiredArgsConstructor
public class AlbumController {
    private final AlbumService albumService;
    private final UserRecommendationService userRecommendationService;

    @GetMapping
    public List<AlbumCardDTO> getAlbums() {
        return albumService.getAlbums();
    }

    @GetMapping("/{id}")
    public AlbumDTO getAlbum(@PathVariable Long id) {
        return albumService.findById(id);
    }

    @GetMapping("/ids")
    public List<AlbumCardDTO> getAlbumsIn(@RequestParam List<Long> ids) {
        return albumService.findByIdsIn(ids);
    }

    @GetMapping("/recommended/{id}")
    public List<SongDTO> getRecommendedSongs(
            @PathVariable Long id,
            @RequestParam(name = "pageNumber", defaultValue = "0", required = false) long pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "10", required = false) long pageSize) {
        return userRecommendationService.getRecommendationsForAlbum(id, pageSize, pageNumber);
    }

    @GetMapping("/search/{name}")
    public List<AlbumCardDTO> getAlbumByNameLike(
            @PathVariable String name,
            @RequestParam(name = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "10", required = false) int pageSize) {
        return albumService.findByNameLike(name, pageNumber, pageSize);
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public AlbumDTO createAlbum(@ModelAttribute AlbumDAO andCreateDTO,
                                @RequestParam(name = "image", required = false) MultipartFile image) {
        return albumService.saveAlbum(andCreateDTO.getProtectionType(), andCreateDTO.getName(), image);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public AlbumDTO patchAlbum(@PathVariable Long id,
                               @ModelAttribute AlbumDAO albumPatchDTO,
                               @RequestParam(name = "image", required = false) MultipartFile image) {
        return albumService.patchAlbum(id, albumPatchDTO.getProtectionType(), albumPatchDTO.getSongIds(), albumPatchDTO.getName(), image);
    }

    @PatchMapping("/add-songs/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public AlbumDTO addSongs(@PathVariable Long id,
                             @RequestParam(name = "song_id", required = false) List<Long> songIds) {
        return albumService.addSongs(id, songIds);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteAlbum(@PathVariable Long id) {
        albumService.deleteAlbum(id);
    }
}
