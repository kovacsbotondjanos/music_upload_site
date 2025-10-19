package unit.userTest;

import com.musicUpload.dataHandler.DTOs.UserDTO;
import com.musicUpload.dataHandler.details.UserDetailsImpl;
import com.musicUpload.dataHandler.enums.Privilege;
import com.musicUpload.dataHandler.models.implementations.User;
import com.musicUpload.dataHandler.repositories.UserRepository;
import com.musicUpload.dataHandler.services.MinioService;
import com.musicUpload.dataHandler.services.UserService;
import com.musicUpload.exceptions.NotAcceptableException;
import com.musicUpload.exceptions.UnauthenticatedException;
import com.musicUpload.exceptions.WrongFormatException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;
    @Mock
    private UserDetailsImpl mockUserDetails;
    @Mock
    private MinioService minioService;

    @InjectMocks
    private UserService userService;
    private User user;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void onSetUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        user = new User(
                null,
                null,
                null,
                null,
                "user",
                List.of(),
                List.of(),
                Privilege.USER,
                List.of(),
                List.of(),
                null,
                null
        );
        when(minioService.getImageMap(anyList())).thenReturn(Map.of());
        when(minioService.getImage(anyString())).thenReturn(null);
    }

    @AfterEach
    void closeMocks() throws Exception {
        autoCloseable.close();
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
        //Then
        user.setEmail("asd@asd.com");
        user.setUsername("user");
        user.setPassword("12345678");
        assertThrows(NotAcceptableException.class,
                () -> userService.registerUser(user));
    }

    @Test
    void findUserWithoutAuth() {
        assertThrows(UnauthenticatedException.class,
                () -> userService.findCurrUser());
    }

    @Test
    void findUserWithAuth() {
        SecurityContextHolder.setContext(securityContext);
        //Given
        mockUserDetails = new UserDetailsImpl(
                1L,
                null,
                null,
                null,
                null
        );
        given(userRepository.findById(1L))
                .willReturn(Optional.of(user));
        when(authentication.getPrincipal()).thenReturn(mockUserDetails);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        //When
        UserDTO userDTO = userService.findCurrUser();
        //Then
        assertEquals("user", userDTO.getUsername());
        SecurityContextHolder.clearContext();
    }

    @Test
    void findNonExistingUser() {
        //Given
        given(userRepository.findById(1L))
                .willReturn(Optional.empty());
        //Then
        assertThrows(UnauthenticatedException.class,
                () -> userService.findCurrUser());
    }
}
