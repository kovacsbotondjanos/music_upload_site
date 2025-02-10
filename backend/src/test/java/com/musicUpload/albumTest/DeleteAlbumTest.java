package com.musicUpload.albumTest;

import com.musicUpload.dataHandler.details.UserDetailsImpl;
import com.musicUpload.dataHandler.enums.ProtectionType;
import com.musicUpload.dataHandler.models.implementations.Album;
import com.musicUpload.dataHandler.models.implementations.User;
import com.musicUpload.dataHandler.repositories.AlbumRepository;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

public class DeleteAlbumTest {
    private final UserDetailsImpl userDetails = new UserDetailsImpl(1L,
            "user1",
            "pwd",
            List.of(),
            "");
    private final ProtectionType protectionType = ProtectionType.PUBLIC;
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
    @InjectMocks
    private AlbumService albumService;
    private List<Album> albums;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void onSetUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        albums = List.of(
                new Album(1L,
                        "",
                        "foo",
                        protectionType,
                        new User(),
                        new ArrayList<>(),
                        new Date(),
                        new Date()),
                new Album(2L,
                        "",
                        "bar",
                        protectionType,
                        new User(),
                        new ArrayList<>(),
                        new Date(),
                        new Date()),
                new Album(3L,
                        "",
                        "baz",
                        protectionType,
                        new User(),
                        new ArrayList<>(),
                        new Date(),
                        new Date()));
    }

    @AfterEach
    void closeMocks() throws Exception {
        autoCloseable.close();
    }

    @Test
    void canDeleteAlbumWithoutAuth() {
        assertThrows(UnauthenticatedException.class,
                () -> albumService.deleteAlbum(1L));
    }

    @Test
    void canDeleteOtherUsersAlbum() {
        //Given
        User u = new User(userDetails);
        given(userRepository.findById(1L))
                .willReturn(Optional.of(u));
        given(albumRepository.findByUserAndId(u, 2L))
                .willReturn(Optional.empty());

        //Then
        assertThrows(UnauthenticatedException.class,
                () -> albumService.deleteAlbum(userDetails, 2L));
    }

    @Test
    void canDeleteOwnAlbumWithAuth() {
        //Given
        User u = new User(userDetails);
        given(albumRepository.findByUserAndId(u, 1L))
                .willReturn(Optional.of(albums.getFirst()));
        given(userRepository.findById(1L))
                .willReturn(Optional.of(u));
        Album a = albumService.deleteAlbum(userDetails, 1L);
        //Then
        assertEquals(albums.getFirst(), a);
        assertFalse(u.getAlbums().contains(a));
    }
}
