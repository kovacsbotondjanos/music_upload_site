package com.musicUpload.userTest;

import com.musicUpload.dataHandler.models.Auth;
import com.musicUpload.dataHandler.models.User;
import com.musicUpload.dataHandler.repositories.UserRepository;
import com.musicUpload.dataHandler.seeder.factories.UserFactory;
import com.musicUpload.dataHandler.services.AuthService;
import com.musicUpload.dataHandler.services.UserService;
import com.musicUpload.exceptions.EmailInWrongFormatException;
import com.musicUpload.exceptions.EmptyFieldException;
import com.musicUpload.exceptions.NameInWrongFormatException;
import com.musicUpload.exceptions.PasswordInWrongFormatException;
import com.musicUpload.util.ImageFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RegisterUserTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthService authService;
    @Mock
    private ImageFactory imageFactory;

    @Autowired
    private UserFactory userFactory;
    private UserService userService;
    private User user;

    @BeforeEach
    void onSetUp(){
        MockitoAnnotations.initMocks(this);
        user = new User();
        userService = new UserService(userRepository, authService, imageFactory);
    }

    @Test
    void registerWithNoInfo(){
        user = new User();
        assertThrows(EmptyFieldException.class,
                () -> userService.registerUser(user));
    }

    @Test
    void registerUserWithWrongPassword(){
        user.setEmail("");
        user.setUsername("");
        user.setPassword("1234567");
        assertThrows(PasswordInWrongFormatException.class,
                () -> userService.registerUser(user));
    }

    @Test
    void registerUserWithWrongName(){
        user.setEmail("");
        user.setUsername("");
        user.setPassword("12345678");
        assertThrows(NameInWrongFormatException.class,
                () -> userService.registerUser(user));
    }

    @Test
    void registerUserWithWrongEmail(){
        user.setEmail("asd@");
        user.setUsername("user");
        user.setPassword("12345678");
        assertThrows(EmailInWrongFormatException.class,
                () -> userService.registerUser(user));
    }

    @Test
    void registerUserWithDuplicatedUsername(){
        //Given
        given(userRepository.findByUsername("user"))
                .willReturn(Optional.of(new User()));
        given(authService.getByName("USER"))
                .willReturn(Optional.of(new Auth()));
        //Then
        user.setEmail("asd@asd.com");
        user.setUsername("user");
        user.setPassword("12345678");
        assertThrows(NameInWrongFormatException.class,
                () -> userService.registerUser(user));
    }

    @Test
    void registerUserWithDuplicatedEmail(){
        //Given
        given(userRepository.findByEmail("asd@asd.com"))
                .willReturn(Optional.of(new User()));
        given(authService.getByName("USER"))
                .willReturn(Optional.of(new Auth()));
        //Then
        user.setEmail("asd@asd.com");
        user.setUsername("user");
        user.setPassword("12345678");
        assertThrows(EmailInWrongFormatException.class,
                () -> userService.registerUser(user));
    }
}
