package com.musicUpload.controllers.endpoint;

import com.musicUpload.dataHandler.DTOs.SongDAO;
import com.musicUpload.dataHandler.DTOs.SongDTO;
import com.musicUpload.dataHandler.services.SongService;
import com.musicUpload.dataHandler.services.UserRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1/songs")
@CrossOrigin
public class SongController {
    private final SongService songService;
    private final UserRecommendationService userRecommendationService;

    @Autowired
    public SongController(SongService songService,
                          UserRecommendationService userRecommendationService) {
        this.songService = songService;
        this.userRecommendationService = userRecommendationService;
    }

    @GetMapping
    public List<SongDTO> getSongs() {
        return songService.getSongs();
    }

    @GetMapping("/{id}")
    public SongDTO getSongById(@PathVariable Long id) {
        return songService.findById(id);
    }

    @GetMapping("/ids")
    public List<SongDTO> getSongsIn(@RequestParam List<Long> ids) {
        return songService.findByIdsIn(ids);
    }

    @GetMapping("/search/{name}")
    public List<SongDTO> getSongByNameLike(@PathVariable String name,
                                           @RequestParam(name = "pageNumber", defaultValue = "0", required = false) int pageNumber,
                                           @RequestParam(name = "pageSize", defaultValue = "10", required = false) int pageSize) {
        return songService.findByNameLike(name, pageNumber, pageSize);
    }

    @GetMapping("/recommended/{id}")
    public List<SongDTO> getRecommendedSongs(@PathVariable Long id) {
        return userRecommendationService.getRecommendationsForSong(id);
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public void createSong(@ModelAttribute SongDAO songDAO,
                           @RequestParam(name = "image", required = false) MultipartFile image,
                           @RequestParam(name = "song") MultipartFile song) {

        songService.addSong(
                songDAO,
                image,
                song
        );
    }

    @PatchMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void patchSong(@PathVariable Long id,
                          @ModelAttribute SongDAO songPatchDTO,
                          @RequestParam(name = "image", required = false) MultipartFile image) {
        songService.patchSong(
                id,
                songPatchDTO,
                image
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteSong(@PathVariable Long id) {
        songService.deleteSong(id);
    }
}
