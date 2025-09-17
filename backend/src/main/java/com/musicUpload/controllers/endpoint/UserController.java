package com.musicUpload.controllers.endpoint;

import com.musicUpload.dataHandler.DTOs.*;
import com.musicUpload.dataHandler.models.implementations.User;
import com.musicUpload.dataHandler.services.UserRecommendationService;
import com.musicUpload.dataHandler.services.UserService;
import com.musicUpload.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
@CrossOrigin
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserRecommendationService userRecommendationService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<LogInResponseDTO> login(@RequestBody LogInDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return ResponseEntity.ok(
                LogInResponseDTO.builder()
                        .token(jwtUtils.generateToken(authentication))
                        .build()
        );
    }

    @GetMapping
    public UserDTO getCurrUser(HttpServletRequest request) {
        return userService.findCurrUser();
    }

    @GetMapping("/search/{name}")
    public List<FilteredUserDTO> getSongByNameLike(@PathVariable String name,
                                                   @RequestParam(name = "pageNumber", defaultValue = "0", required = false) int pageNumber,
                                                   @RequestParam(name = "pageSize", defaultValue = "10", required = false) int pageSize) {
        return userService.findByNameLike(name, pageNumber, pageSize);
    }

    @GetMapping("/{id}")
    public FilteredUserDTO getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/recommended")
    public List<SongDTO> getRecommendedSongs() {
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
