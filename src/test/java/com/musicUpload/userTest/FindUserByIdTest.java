package com.musicUpload.userTest;

import com.musicUpload.dataHandler.DTOs.UserDTO;
import com.musicUpload.dataHandler.details.CustomUserDetails;
import com.musicUpload.dataHandler.models.Auth;
import com.musicUpload.dataHandler.models.User;
import com.musicUpload.dataHandler.repositories.UserRepository;
import com.musicUpload.dataHandler.services.AuthService;
import com.musicUpload.dataHandler.services.UserService;
import com.musicUpload.exceptions.UnauthenticatedException;
import com.musicUpload.util.ImageFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

public class FindUserByIdTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthService authService;
    @Mock
    private ImageFactory imageFactory;

    private UserService userService;
    private User user;

    @BeforeEach
    void onSetUp(){
        MockitoAnnotations.initMocks(this);
        user = new User(null, null, null, null,
                        "user", List.of(), List.of(), new Auth(), List.of(), List.of(), null, null);
        userService = new UserService(userRepository, authService, imageFactory);
    }

    @Test
    void findUserWithoutAuth(){
        assertThrows(UnauthenticatedException.class,
                () -> userService.findCurrUser(null));
    }

    @Test
    void findUserWithAuth(){
        //Given
        CustomUserDetails userDetails = new CustomUserDetails(
                1L,
                null,
                null,
                null,
                null,
                null,
                null
        );
        given(userRepository.findById(1L))
                .willReturn(Optional.of(user));
        //When
        UserDTO userDTO = userService.findCurrUser(userDetails);
        //Then
        assertEquals("user", userDTO.getUsername());
    }

    @Test
    void findNonExistingUser(){
        //Given
        CustomUserDetails userDetails = new CustomUserDetails(
                1L,
                null,
                null,
                null,
                null,
                null,
                null
        );
        given(userRepository.findById(1L))
                .willReturn(Optional.empty());
        //Then
        assertThrows(UnauthenticatedException.class,
                () -> userService.findCurrUser(null));
    }
}
