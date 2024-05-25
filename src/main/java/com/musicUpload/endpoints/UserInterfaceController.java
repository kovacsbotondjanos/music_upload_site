package com.musicUpload.endpoints;

import com.musicUpload.databaseHandler.models.Song;
import com.musicUpload.databaseHandler.services.SongService;
import com.musicUpload.databaseHandler.details.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class UserInterfaceController {
    @Autowired
    private final SongService songService;

    public UserInterfaceController(SongService songService) {
        this.songService = songService;
    }

    @GetMapping("/home")
    public String getHomePage(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            model.addAttribute("profilePicture", userDetails.getProfilePicture());
            model.addAttribute("userName", userDetails.getUsername());
            model.addAttribute("id", userDetails.getId());
            model.addAttribute("loggedIn", true);
        }
        else{
            model.addAttribute("loggedIn", false);
        }

        List<Song> songs = songService.getRandomSongs();
        model.addAttribute("songs", songs);

        return "home";
    }

    @GetMapping("/profile")
    public String getProfile(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            model.addAttribute("profilePicture", userDetails.getProfilePicture());
            model.addAttribute("userName", userDetails.getUsername());
            model.addAttribute("id", userDetails.getId());

            return "user";
        }
        return "unauthenticated";
    }

    @GetMapping("/login")
    public String auth(){
        return "login";
    }
}
