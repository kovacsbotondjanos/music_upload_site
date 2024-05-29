package com.musicUpload.controllers;

import com.musicUpload.dataHandler.DTOs.UserDTO;
import com.musicUpload.dataHandler.details.CustomUserDetails;
import com.musicUpload.dataHandler.models.User;
import com.musicUpload.dataHandler.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
    public UserDTO getCurrUser(@AuthenticationPrincipal CustomUserDetails userDetails){
        return userService.findCurrUser(userDetails);
    }

    @PostMapping("/add")
    public ResponseEntity<String> createUser(@ModelAttribute User user){
        userService.registerUser(user);
        return new ResponseEntity<>("successfully created user", HttpStatus.CREATED);
    }
}
