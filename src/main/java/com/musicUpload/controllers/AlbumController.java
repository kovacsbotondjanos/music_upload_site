package com.musicUpload.controllers;

import com.musicUpload.dataHandler.DTOs.AlbumDTO;
import com.musicUpload.dataHandler.models.Album;
import com.musicUpload.dataHandler.services.AlbumService;
import com.musicUpload.dataHandler.services.SongService;
import com.musicUpload.dataHandler.details.CustomUserDetails;
import com.musicUpload.dataHandler.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping("/albums")
@CrossOrigin
public class AlbumController {
    @Autowired
    private final SongService songService;
    @Autowired
    private final AlbumService albumService;
    @Autowired
    private final UserService userService;

    public AlbumController(SongService songService, AlbumService albumService, UserService userService) {
        this.songService = songService;
        this.albumService = albumService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> getAlbums(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return new ResponseEntity<>(userDetails.getAlbums().stream().map(AlbumDTO::new).toList(), HttpStatus.OK);
        }
        return new ResponseEntity<>("unauthorized", HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAlbum(@PathVariable Long id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            Optional<Album> albumOptional = userDetails.getAlbums().stream().filter(a -> a.getId().equals(id)).findAny();

            if(albumOptional.isPresent()){
                return new ResponseEntity<>(AlbumDTO.of(albumOptional.get()), HttpStatus.OK);
            }
            else{
                Optional<Album> albumOptional2 = albumService.findById(id);
                if(albumOptional2.isPresent() && !albumOptional2.get().getProtectionType().getName().equals("PRIVATE")){
                    return new ResponseEntity<>(AlbumDTO.of(albumOptional2.get()), HttpStatus.OK);
                }
                return new ResponseEntity<>("unauthorized", HttpStatus.UNAUTHORIZED);
            }
        }
        return new ResponseEntity<>("unauthorized", HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createAlbum(@RequestParam(name = "protection_type") String protectionType,
                                         @RequestParam(name = "name") String name,
                                         @RequestParam(name = "image", required = false) MultipartFile image){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            //TODO: create a function for this in the AlbumService
        }
        return new ResponseEntity<>("unauthorized", HttpStatus.UNAUTHORIZED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> patchAlbum(@PathVariable Long id,
                                        @RequestParam(name = "protection_type", required = false) String protectionType,
                                        @RequestParam(name = "song_id", required = false) Long songId,
                                        @RequestParam(name = "name", required = false) String name,
                                        @RequestParam(name = "image", required = false) MultipartFile image){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            //TODO: create a function for this in the AlbumService
        }
        return new ResponseEntity<>("unauthorized", HttpStatus.UNAUTHORIZED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAlbum(@PathVariable Long id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {

            Optional<Album> albumOptional = userDetails.getAlbums().stream().filter(a -> a.getId().equals(id)).findAny();

            if(albumOptional.isPresent()){
                userDetails.getAlbums().remove(albumOptional.get());

                new Thread(() -> albumService.deleteAlbum(albumOptional.get())).start();

                return new ResponseEntity<>(userDetails.getAlbums().stream().map(AlbumDTO::new).toList(), HttpStatus.NO_CONTENT);
            }
            else{
                return new ResponseEntity<>("unauthorized", HttpStatus.UNAUTHORIZED);
            }
        }
        return new ResponseEntity<>("unauthorized", HttpStatus.UNAUTHORIZED);
    }
}
