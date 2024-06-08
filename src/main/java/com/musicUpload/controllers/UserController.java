package com.musicUpload.controllers;

import com.musicUpload.dataHandler.DTOs.UserDTO;
import com.musicUpload.dataHandler.details.CustomUserDetails;
import com.musicUpload.dataHandler.models.implementations.User;
import com.musicUpload.dataHandler.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public UserDTO getCurrUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return userService.findCurrUser(userDetails);
    }

    @PostMapping("/add")
    @ResponseStatus(value = HttpStatus.CREATED)
    public void createUser(@ModelAttribute User user) {
        userService.registerUser(user);
    }

    @PatchMapping
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void patchUser(@AuthenticationPrincipal CustomUserDetails userDetails,
                          @RequestParam(name = "username", required = false) String username,
                          @RequestParam(name = "email", required = false) String email,
                          @RequestParam(name = "new_password", required = false) String password,
                          @RequestParam(name = "old_password", required = false) String oldPassword,
                          @RequestParam(name = "image", required = false) MultipartFile image) {
        userService.patchUser(userDetails,
                username,
                email,
                password,
                oldPassword,
                image);
    }

    @DeleteMapping
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.deleteUser(userDetails);
    }
}
