package com.musicUpload.albumTest;

import com.musicUpload.dataHandler.details.CustomUserDetails;
import com.musicUpload.dataHandler.models.Album;
import com.musicUpload.dataHandler.models.Auth;
import com.musicUpload.dataHandler.models.ProtectionType;
import com.musicUpload.dataHandler.models.User;
import com.musicUpload.dataHandler.repositories.AlbumRepository;
import com.musicUpload.dataHandler.repositories.UserRepository;
import com.musicUpload.dataHandler.services.AlbumService;
import com.musicUpload.dataHandler.services.ProtectionTypeService;
import com.musicUpload.dataHandler.services.SongService;
import com.musicUpload.exceptions.UnauthenticatedException;
import com.musicUpload.util.ImageFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DeleteAlbumTest {
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProtectionTypeService protectionTypeService;
    @Mock
    private SongService songService;
    @Mock
    private ImageFactory imageFactory;

    private AlbumService albumService;
    private List<Album> albums;
    private CustomUserDetails userDetails = new CustomUserDetails(1L,
            "user1",
            "pwd",
            List.of(),
            "",
            List.of(),
            List.of());
    private ProtectionType protectionType = new ProtectionType(1L, "PUBLIC", new ArrayList<>(), new ArrayList<>());

    @BeforeEach
    void onSetUp(){
        MockitoAnnotations.initMocks(this);
        albumService = new AlbumService(albumRepository,
                userRepository,
                protectionTypeService,
                songService,
                imageFactory);
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

    @Test
    void canDeleteAlbumWithoutAuth(){
        assertThrows(UnauthenticatedException.class,
                () -> albumService.deleteAlbum(null, 1L));
    }

    @Test
    void canDeleteOtherUsersAlbum(){
        CustomUserDetails userDetails = new CustomUserDetails(1L,
                "user1",
                "",
                List.of(),
                "",
                List.of(),
                List.of(albums.get(0)));
        assertThrows(UnauthenticatedException.class,
                () -> albumService.deleteAlbum(userDetails, 2L));
    }

    @Test
    void canDeleteOwnAlbumWithAuth(){
        CustomUserDetails userDetails = new CustomUserDetails(1L,
                "user1",
                "",
                List.of(),
                "",
                List.of(),
                new ArrayList<>(List.of(albums.get(0))));
        Album a = albumService.deleteAlbum(userDetails, 1L);
        assertEquals(albums.get(0), a);
        assertFalse(userDetails.getAlbums().contains(a));
    }
}