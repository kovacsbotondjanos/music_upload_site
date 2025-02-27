package unit.albumTest;

import com.musicUpload.dataHandler.DTOs.SongDTO;
import com.musicUpload.dataHandler.details.UserDetailsImpl;
import com.musicUpload.dataHandler.enums.Privilege;
import com.musicUpload.dataHandler.enums.ProtectionType;
import com.musicUpload.dataHandler.models.implementations.Album;
import com.musicUpload.dataHandler.models.implementations.Song;
import com.musicUpload.dataHandler.models.implementations.User;
import com.musicUpload.dataHandler.repositories.AlbumRepository;
import com.musicUpload.dataHandler.repositories.SongRepository;
import com.musicUpload.dataHandler.repositories.UserRepository;
import com.musicUpload.dataHandler.services.AlbumService;
import com.musicUpload.dataHandler.services.MinioService;
import com.musicUpload.dataHandler.services.SongService;
import com.musicUpload.exceptions.UnauthenticatedException;
import com.musicUpload.util.ImageFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

public class UpdateAlbumTest {
    private final ProtectionType publicProtectionType = ProtectionType.PUBLIC;
    private final ProtectionType privateProtectionType = ProtectionType.PRIVATE;
    private final UserDetailsImpl userDetails = new UserDetailsImpl(
            1L,
            "user1",
            "pwd",
            List.of(),
            ""
    );
    private final Song song = new Song(
            1L,
            "",
            "foo",
            "",
            0L,
            privateProtectionType,
            new User(),
            new ArrayList<>(),
            new HashSet<>(),
            new Date(),
            new Date()
    );
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SongService songService;
    @Mock
    private ImageFactory imageFactory;
    @Mock
    private MinioService minioService;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;
    @Mock
    private SongRepository songRepository;
    @InjectMocks
    private AlbumService albumService;
    private Album album;
    private Long id;
    private User user;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void onSetUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        id = 1L;
        album = new Album(
                id,
                "",
                "foo",
                publicProtectionType,
                new User(),
                new ArrayList<>(),
                new Date(),
                new Date()
        );
        user = new User(
                1L,
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
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @AfterEach
    void closeMocks() throws Exception {
        autoCloseable.close();
    }

    @Test
    void updateAlbumWithoutAuth() {
        assertThrows(UnauthenticatedException.class,
                () -> albumService.patchAlbum(null, null, null, null, null));
    }

    @Test
    void updateOtherUsersAlbum() {
        assertThrows(UnauthenticatedException.class,
                () -> albumService.patchAlbum(2L, null, null, null, null));
    }

    @Test
    void updateNameTest() {
        User u = new User(userDetails);
        SecurityContextHolder.setContext(securityContext);
        given(albumRepository.findByUserAndId(u, 1L))
                .willReturn(Optional.of(album));
        given(userRepository.findById(userDetails.getId()))
                .willReturn(Optional.of(u));
        albumService.patchAlbum(
                1L,
                null, null,
                "bar",
                null
        );
        assertEquals("bar", album.getName());
        SecurityContextHolder.clearContext();
    }

    @Test
    void updateProtectionType() {
        User u = new User(userDetails);
        SecurityContextHolder.setContext(securityContext);
        given(albumRepository.findByUserAndId(u, 1L))
                .willReturn(Optional.of(album));
        given(userRepository.findById(userDetails.getId()))
                .willReturn(Optional.of(u));
        albumService.patchAlbum(
                1L,
                "PRIVATE", null,
                null,
                null
        );
        assertEquals("PRIVATE", album.getProtectionType().getName());
        SecurityContextHolder.clearContext();
    }

    @Test
    void updateAlbumWithPrivateSongNotOwned() {
        song.setProtectionType(privateProtectionType);
        User u = new User(userDetails);
        SecurityContextHolder.setContext(securityContext);
        given(albumRepository.findByUserAndId(u, 1L))
                .willReturn(Optional.of(album));
        given(userRepository.findById(userDetails.getId()))
                .willReturn(Optional.of(u));
        albumService.patchAlbum(
                1L,
                null,
                List.of(1L),
                null, null
        );
        assertEquals(List.of(), album.getSongs());
        SecurityContextHolder.clearContext();
    }

    @Test
    void updateAlbumWithPublicSongNotOwned() {
        song.setProtectionType(publicProtectionType);
        User u = new User(userDetails);
        SecurityContextHolder.setContext(securityContext);
        given(albumRepository.findByUserAndId(u, 1L))
                .willReturn(Optional.of(album));
        given(userRepository.findById(userDetails.getId()))
                .willReturn(Optional.of(u));
        given(songService.findById(1L))
                .willReturn(SongDTO.of(song));
        given(songRepository.findById(1L))
                .willReturn(Optional.of(song));
        albumService.patchAlbum(
                1L,
                null,
                List.of(1L),
                null,
                null
        );
        assertEquals(List.of(song), album.getSongs());
        SecurityContextHolder.clearContext();
    }

    @Test
    void updateAlbumWithPrivateSongOwned() {
        song.setProtectionType(privateProtectionType);
        User u = new User(userDetails);
        SecurityContextHolder.setContext(securityContext);
        given(albumRepository.findByUserAndId(u, 1L))
                .willReturn(Optional.of(album));
        given(userRepository.findById(userDetails.getId()))
                .willReturn(Optional.of(u));
        album.setUser(user);
        user.setAlbums(List.of(album));
        user.setSongs(List.of(song));
        given(songService.findById(1L))
                .willReturn(SongDTO.of(song));
        albumService.patchAlbum(
                1L,
                null,
                List.of(1L),
                null,
                null
        );
        SecurityContextHolder.clearContext();
    }
}
