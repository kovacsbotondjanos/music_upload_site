package com.musicUpload.endpoints.endpointControllers;

import com.musicUpload.databaseHandler.models.albums.Album;
import com.musicUpload.databaseHandler.models.albums.AlbumService;
import com.musicUpload.databaseHandler.models.songs.Song;
import com.musicUpload.databaseHandler.models.songs.SongService;
import com.musicUpload.databaseHandler.models.users.CustomUserDetails;
import com.musicUpload.databaseHandler.models.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping("/songs")
public class SongController {
    //TODO: create endpoints for songs that can be listened from without access

    @Autowired
    private final SongService songService;
    @Autowired
    private final AlbumService albumService;
    @Autowired
    private final UserService userService;

    public SongController(SongService songService, AlbumService albumService, UserService userService) {
        this.songService = songService;
        this.albumService = albumService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> getSongs(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return new ResponseEntity<>(userDetails.getSongs(), HttpStatus.OK);
        }
        return new ResponseEntity<>("unauthorized", HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSong(@PathVariable Long id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            Optional<Song> songOptional = userDetails.getSongs().stream().filter(s -> s.getId().equals(id)).findAny();

            if(songOptional.isPresent()){
                return new ResponseEntity<>(songOptional.get(), HttpStatus.OK);
            }
            else{
                Optional<Song> songOptional2 = songService.findById(id);
                if(songOptional2.isPresent() && !songOptional2.get().getProtectionType().getName().equals("PRIVATE")){
                    return new ResponseEntity<>(songOptional2.get(), HttpStatus.OK);
                }
                return new ResponseEntity<>("unauthorized", HttpStatus.UNAUTHORIZED);
            }
        }
        return new ResponseEntity<>("unauthorized", HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createSong(@RequestParam(name = "protection_type") String protectionType,
                                        @RequestParam(name = "name") String name,
                                        @RequestParam(name = "image", required = false) MultipartFile image,
                                        @RequestParam(name = "song") MultipartFile song){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {

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

                return new ResponseEntity<>(userDetails.getAlbums(), HttpStatus.NO_CONTENT);
            }
            else{
                return new ResponseEntity<>("unauthorized", HttpStatus.UNAUTHORIZED);
            }
        }
        return new ResponseEntity<>("unauthorized", HttpStatus.UNAUTHORIZED);
    }
}
