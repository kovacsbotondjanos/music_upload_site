package com.musicUpload.controllers;

import com.musicUpload.dataHandler.DTOs.UserDAO;
import com.musicUpload.dataHandler.DTOs.UserDTO;
import com.musicUpload.dataHandler.details.UserDetailsImpl;
import com.musicUpload.dataHandler.models.implementations.User;
import com.musicUpload.dataHandler.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
@CrossOrigin
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public UserDTO getCurrUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.findCurrUser(userDetails);
    }

    @GetMapping("/search/{name}")
    public List<UserDTO> getSongByNameLike(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                           @PathVariable String name) {
        return userService.findByNameLike(userDetails, name);
    }

    @PostMapping("/add")
    @ResponseStatus(value = HttpStatus.CREATED)
    public void createUser(@ModelAttribute User user) {
        userService.registerUser(user);
    }

    @PostMapping("/follow")
    @ResponseStatus(value = HttpStatus.CREATED)
    public void followUser(@AuthenticationPrincipal UserDetailsImpl userDetails,
                           @RequestParam(name = "userId") Long userId) {
        userService.followUser(userDetails, userId);
    }

    @PatchMapping
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void patchUser(@AuthenticationPrincipal UserDetailsImpl userDetails,
                          @ModelAttribute UserDAO userPatch,
                          @RequestParam(name = "image", required = false) MultipartFile image) {
        userService.patchUser(userDetails,
                userPatch.getUsername(),
                userPatch.getEmail(),
                userPatch.getPassword(),
                userPatch.getOldPassword(),
                image);
    }

    @DeleteMapping
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        userService.deleteUser(userDetails);
    }
}
