package com.musicUpload.controllers;

import com.musicUpload.dataHandler.DTOs.UserDTO;
import com.musicUpload.dataHandler.details.CustomUserDetails;
import com.musicUpload.dataHandler.models.User;
import com.musicUpload.dataHandler.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@CrossOrigin
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> getCurrUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            UserDTO user = userService.findUserById(userDetails.getId());
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        return new ResponseEntity<>("unauthenticated", HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/add")
    public ResponseEntity<?> createUser(@ModelAttribute User user){
        userService.registerUser(user);
        return new ResponseEntity<>("successfully created user", HttpStatus.CREATED);
    }
}
