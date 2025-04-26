package com.musicUpload.controllers.endpoint;

import com.musicUpload.dataHandler.DTOs.AlbumDAO;
import com.musicUpload.dataHandler.DTOs.AlbumDTO;
import com.musicUpload.dataHandler.DTOs.SongDTO;
import com.musicUpload.dataHandler.services.AlbumService;
import com.musicUpload.dataHandler.services.UserRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1/albums")
@CrossOrigin
public class AlbumController {
    private final AlbumService albumService;
    private final UserRecommendationService userRecommendationService;

    @Autowired
    public AlbumController(AlbumService albumService,
                           UserRecommendationService userRecommendationService) {
        this.albumService = albumService;
        this.userRecommendationService = userRecommendationService;
    }

    @GetMapping
    public List<AlbumDTO> getAlbums() {
        return albumService.getAlbums();
    }

    @GetMapping("/{id}")
    public AlbumDTO getAlbum(@PathVariable Long id) {
        return albumService.findById(id);
    }

    @GetMapping("/ids")
    public List<AlbumDTO> getAlbumsIn(@RequestParam List<Long> ids) {
        return albumService.findByIdsIn(ids);
    }

    @GetMapping("/recommended/{id}")
    public List<SongDTO> getRecommendedSongs(@PathVariable Long id) {
        return userRecommendationService.getRecommendationsForAlbum(id);
    }

    @GetMapping("/search/{name}")
    public List<AlbumDTO> getAlbumByNameLike(@PathVariable String name,
                                             @RequestParam(name = "pageNumber", defaultValue = "0", required = false) int pageNumber,
                                             @RequestParam(name = "pageSize", defaultValue = "10", required = false) int pageSize) {
        return albumService.findByNameLike(name, pageNumber, pageSize);
    }

    @GetMapping("/recommendations/{id}")
    public List<Long> getRecommendedSongsForAlbum(@PathVariable Long id) {
        return null;
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public AlbumDTO createAlbum(@ModelAttribute AlbumDAO andCreateDTO,
                            @RequestParam(name = "image", required = false) MultipartFile image) {
        return AlbumDTO.of(
                albumService.saveAlbum(
                    andCreateDTO.getProtectionType(),
                    andCreateDTO.getName(),
                    image
                )
        );
    }

    @PatchMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public AlbumDTO patchAlbum(@PathVariable Long id,
                               @ModelAttribute AlbumDAO albumPatchDTO,
                               @RequestParam(name = "image", required = false) MultipartFile image) {
        return AlbumDTO.of(
                albumService.patchAlbum(
                        id,
                        albumPatchDTO.getProtectionType(),
                        albumPatchDTO.getSongIds(),
                        albumPatchDTO.getName(),
                        image
                )
        );
    }

    @PatchMapping("/add-songs/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public AlbumDTO addSongs(@PathVariable Long id,
                             @RequestParam(name = "song_id", required = false) List<Long> songIds) {
        return AlbumDTO.of(albumService.addSongs(id, songIds));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteAlbum(@PathVariable Long id) {
        albumService.deleteAlbum(id);
    }
}
