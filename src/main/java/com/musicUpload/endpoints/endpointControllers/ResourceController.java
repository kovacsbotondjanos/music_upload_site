package com.musicUpload.endpoints.endpointControllers;


import com.musicUpload.databaseHandler.models.Song;
import com.musicUpload.databaseHandler.services.SongService;
import com.musicUpload.databaseHandler.details.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@RestController
@CrossOrigin
public class ResourceController {
    @Autowired
    private final SongService songService;
    private final String imagePathName = "images\\";
    private final String musicPathName = "music\\";

    public ResourceController(SongService songService) {
        this.songService = songService;
    }

    @GetMapping("/images/{name}")
    public ResponseEntity<Resource> getImage(@PathVariable String name){
        try{
            Path imagePath = Paths.get(imagePathName).resolve(name);
            Resource resource = new UrlResource(imagePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok().body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException me) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/music/{nameHashed}")
    public ResponseEntity<Resource> getSong(@PathVariable String nameHashed){

        Path path = Paths.get(musicPathName);

        Optional<Song> songOptional = songService.findByNameHashed(nameHashed);
        if(songOptional.isPresent() && !songOptional.get().getProtectionType().getName().equals("PRIVATE")){
            try{
                Path imagePath = path.resolve(songOptional.get().getNameHashed());
                Resource resource = new UrlResource(imagePath.toUri());
                return ResponseEntity.ok().body(resource);
            }
            catch (IOException e){
                return ResponseEntity.notFound().build();
            }
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            Optional<Song> songOptionalForUser = userDetails.getSongs().stream().filter(s -> s.getName().equals(nameHashed)).findAny();

            if(songOptionalForUser.isPresent()){
                try{
                    Path imagePath = path.resolve(songOptionalForUser.get().getNameHashed());
                    Resource resource = new UrlResource(imagePath.toUri());
                    return ResponseEntity.ok().body(resource);
                }
                catch (IOException e){
                    return ResponseEntity.notFound().build();
                }
            }
        }
        return ResponseEntity.notFound().build();
    }
}
