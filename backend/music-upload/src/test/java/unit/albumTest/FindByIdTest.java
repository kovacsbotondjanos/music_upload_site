package unit.albumTest;

import com.musicUpload.dataHandler.DTOs.AlbumDTO;
import com.musicUpload.dataHandler.details.UserDetailsImpl;
import com.musicUpload.dataHandler.enums.ProtectionType;
import com.musicUpload.dataHandler.models.implementations.Album;
import com.musicUpload.dataHandler.models.implementations.User;
import com.musicUpload.dataHandler.repositories.AlbumRepository;
import com.musicUpload.dataHandler.services.AlbumService;
import com.musicUpload.exceptions.NotFoundException;
import com.musicUpload.exceptions.UnauthenticatedException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

public class FindByIdTest {
    private final ProtectionType privateprotectionType = ProtectionType.PRIVATE;
    UserDetailsImpl userDetails = new UserDetailsImpl(
            1L,
            "user1",
            "pwd",
            List.of(),
            ""
    );
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private AlbumService albumService;
    private Album album;
    private Long id;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void onSetUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        id = 1L;
        album = new Album(
                id,
                "",
                "foo",
                ProtectionType.PUBLIC,
                new User(),
                new ArrayList<>(),
                new Date(),
                new Date()
        );
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @AfterEach
    void closeMocks() throws Exception {
        autoCloseable.close();
    }

    @Test
    void canFindByIdPublicAlbum() {
        //Given
        given(albumRepository.findById(id))
                .willReturn(Optional.of(album));
        //When
        AlbumDTO albumDTO = albumService.findById(id);
        //Then
        assertEquals("foo", albumDTO.getName());
    }

    @Test
    void canFindByIdNonExistingWithAuthAlbum() {
        //Given
        album.setId(2L);
        given(albumRepository.findById(id))
                .willReturn(Optional.empty());
        //Then
        assertThrows(NotFoundException.class,
                () -> albumService.findById(id));
    }

    @Test
    void canFindByIdPrivateNoAuth() {
        //Given
        album.setProtectionType(privateprotectionType);
        given(albumRepository.findById(id))
                .willReturn(Optional.of(album));
        //Then
        assertThrows(UnauthenticatedException.class,
                () -> albumService.findById(id));
    }

    @Test
    void canFindByIdPrivateWithUser() {
        //Given
        album.setProtectionType(privateprotectionType);
        album.setUser(new User(userDetails));
        SecurityContextHolder.setContext(securityContext);
        given(albumRepository.findById(id))
                .willReturn(Optional.of(album));
        //When
        AlbumDTO a = albumService.findById(id);
        //Then
        assertEquals("foo", a.getName());
        SecurityContextHolder.clearContext();
    }
}
