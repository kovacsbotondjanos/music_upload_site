package com.musicUpload.userTest;

import com.musicUpload.dataHandler.DTOs.UserDTO;
import com.musicUpload.dataHandler.details.CustomUserDetails;
import com.musicUpload.dataHandler.models.implementations.Auth;
import com.musicUpload.dataHandler.models.implementations.User;
import com.musicUpload.dataHandler.repositories.UserRepository;
import com.musicUpload.dataHandler.services.AuthService;
import com.musicUpload.dataHandler.services.UserService;
import com.musicUpload.exceptions.NotAcceptableException;
import com.musicUpload.exceptions.UnauthenticatedException;
import com.musicUpload.exceptions.WrongFormatException;
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

public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthService authService;
    @Mock
    private ImageFactory imageFactory;

    private UserService userService;
    private User user;

    @BeforeEach
    void onSetUp() {
        MockitoAnnotations.initMocks(this);
        user = new User(null, null, null, null,
                "user", List.of(), List.of(), new Auth(), List.of(), List.of(), null, null);
        userService = new UserService(userRepository, authService, imageFactory);
    }

    @Test
    void registerUserWithNoInfo() {
        user = new User();
        assertThrows(WrongFormatException.class,
                () -> userService.registerUser(user));
    }

    @Test
    void registerUserWithWrongPassword() {
        user.setEmail("");
        user.setUsername("");
        user.setPassword("1234567");
        assertThrows(NotAcceptableException.class,
                () -> userService.registerUser(user));
    }

    @Test
    void registerUserWithWrongName() {
        user.setEmail("");
        user.setUsername("");
        user.setPassword("12345678");
        assertThrows(NotAcceptableException.class,
                () -> userService.registerUser(user));
    }

    @Test
    void registerUserWithWrongEmail() {
        user.setEmail("asd@");
        user.setUsername("user");
        user.setPassword("12345678");
        assertThrows(WrongFormatException.class,
                () -> userService.registerUser(user));
    }

    @Test
    void registerUserWithDuplicatedUsername() {
        //Given
        given(userRepository.findByUsername("user"))
                .willReturn(Optional.of(new User()));
        given(authService.getByName("USER"))
                .willReturn(Optional.of(new Auth()));
        //Then
        user.setEmail("asd@asd.com");
        user.setUsername("user");
        user.setPassword("12345678");
        assertThrows(NotAcceptableException.class,
                () -> userService.registerUser(user));
    }

    @Test
    void registerUserWithDuplicatedEmail() {
        //Given
        given(userRepository.findByEmail("asd@asd.com"))
                .willReturn(Optional.of(new User()));
        given(authService.getByName("USER"))
                .willReturn(Optional.of(new Auth()));
        //Then
        user.setEmail("asd@asd.com");
        user.setUsername("user");
        user.setPassword("12345678");
        assertThrows(WrongFormatException.class,
                () -> userService.registerUser(user));
    }

    @Test
    void findUserWithoutAuth() {
        assertThrows(UnauthenticatedException.class,
                () -> userService.findCurrUser(null));
    }

    @Test
    void findUserWithAuth() {
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
    void findNonExistingUser() {
        //Given
        given(userRepository.findById(1L))
                .willReturn(Optional.empty());
        //Then
        assertThrows(UnauthenticatedException.class,
                () -> userService.findCurrUser(null));
    }
}
