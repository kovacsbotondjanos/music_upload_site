package com.musicUpload.controllers;

import com.musicUpload.dataHandler.DTOs.AlbumDTO;
import com.musicUpload.dataHandler.DTOs.SongDTO;
import com.musicUpload.dataHandler.services.AlbumService;
import com.musicUpload.dataHandler.models.Song;
import com.musicUpload.dataHandler.services.SongService;
import com.musicUpload.dataHandler.details.CustomUserDetails;
import com.musicUpload.dataHandler.services.UserService;
import com.musicUpload.util.ImageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<?> getSongs(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return new ResponseEntity<>(userDetails.getSongs().stream().map(SongDTO::new).toList(), HttpStatus.OK);
        }
        return new ResponseEntity<>("unauthorized", HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSongById(@PathVariable Long id){
        Optional<Song> songOptional = songService.findById(id);
        if(songOptional.isPresent() && !songOptional.get().getProtectionType().getName().equals("PRIVATE")){
            return new ResponseEntity<>(SongDTO.of(songOptional.get()), HttpStatus.OK);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            Optional<Song> songOptionalForUser = userDetails.getSongs().stream().filter(s -> s.getId().equals(id)).findAny();

            if(songOptionalForUser.isPresent()){
                return new ResponseEntity<>(SongDTO.of(songOptionalForUser.get()), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("unauthorized", HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/random")
    public ResponseEntity<?> getRandomSongs(){
        List<Song> songs = songService.getRandomSongs();
        return new ResponseEntity<>(songs.stream().map(SongDTO::new).toList(), HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createSong(@RequestParam(name = "protection_type") String protectionType,
                                        @RequestParam(name = "name") String name,
                                        @RequestParam(name = "image", required = false) MultipartFile image,
                                        @RequestParam(name = "song") MultipartFile song){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            //TODO: make these parallel too
            userDetails.addSong(songService.saveSong(
                    protectionType,
                    name,
                    image,
                    song,
                    userDetails.getId()
                    ));
            return ResponseEntity.ok("song created successfully");
        }
        return new ResponseEntity<>("unauthorized", HttpStatus.UNAUTHORIZED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> patchSong(@PathVariable Long id,
                                       @RequestParam(name = "protection_type", required = false) String protectionType,
                                       @RequestParam(name = "name", required = false) String name,
                                       @RequestParam(name = "image", required = false) MultipartFile image){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {

            Optional<Song> songOptional = userDetails.getSongs().stream().filter(s -> s.getId().equals(id)).findAny();
            //TODO: make these parallel too
            songOptional.ifPresent(song ->
                    userDetails.addSong(songService.updateSong(
                            song,
                            protectionType,
                            name,
                            image
                    )));
            return ResponseEntity.ok("song edited successfully");
        }
        return new ResponseEntity<>("unauthorized", HttpStatus.UNAUTHORIZED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSong(@PathVariable Long id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            Optional<Song> songOptional = userDetails.getSongs().stream().filter(s -> s.getId().equals(id)).findAny();
            if(songOptional.isPresent()){
                userDetails.getSongs().remove(songOptional.get());

                new Thread(() -> songService.deleteSong(songOptional.get())).start();

                return new ResponseEntity<>(userDetails.getAlbums().stream().map(AlbumDTO::new).toList(), HttpStatus.NO_CONTENT);
            }
        }
        return new ResponseEntity<>("unauthorized", HttpStatus.UNAUTHORIZED);
    }
}
