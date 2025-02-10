package com.musicUpload.controllers;

import com.musicUpload.dataHandler.DTOs.UserDAO;
import com.musicUpload.dataHandler.DTOs.UserDTO;
import com.musicUpload.dataHandler.models.implementations.User;
import com.musicUpload.dataHandler.services.UserRecommendationService;
import com.musicUpload.dataHandler.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
@CrossOrigin
public class UserController {
    private final UserService userService;
    private final UserRecommendationService userRecommendationService;

    @Autowired
    public UserController(UserService userService,
                          UserRecommendationService userRecommendationService) {
        this.userService = userService;
        this.userRecommendationService = userRecommendationService;
    }

    @GetMapping
    public UserDTO getCurrUser() {
        return userService.findCurrUser();
    }

    @GetMapping("/search/{name}")
    public List<UserDTO> getSongByNameLike(@PathVariable String name,
                                           @RequestParam(name = "pageNumber", defaultValue = "0", required = false) int pageNumber,
                                           @RequestParam(name = "pageSize", defaultValue = "10", required = false) int pageSize) {
        return userService.findByNameLike(name, pageNumber, pageSize);
    }

    @GetMapping("/recommended")
    public List<Long> getRecommendedSongs() {
        return userRecommendationService.getRecommendationsForUser();
    }

    @PostMapping("/add")
    @ResponseStatus(value = HttpStatus.CREATED)
    public void createUser(@ModelAttribute User user) {
        userService.registerUser(user);
    }

    @PostMapping("/follow")
    @ResponseStatus(value = HttpStatus.CREATED)
    public void followUser(@RequestParam(name = "userId") Long userId) {
        userService.followUser(userId);
    }

    @PatchMapping
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void patchUser(@ModelAttribute UserDAO userPatch,
                          @RequestParam(name = "image", required = false) MultipartFile image) {
        userService.patchUser(
                userPatch.getUsername(),
                userPatch.getEmail(),
                userPatch.getPassword(),
                userPatch.getOldPassword(),
                image
        );
    }

    @DeleteMapping
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteUser() {
        userService.deleteUser();
    }
}
