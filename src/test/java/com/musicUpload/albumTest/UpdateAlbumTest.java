package com.musicUpload.albumTest;

import com.musicUpload.cronJobs.EntityCacheManager;
import com.musicUpload.dataHandler.details.UserDetailsImpl;
import com.musicUpload.dataHandler.enums.Privilege;
import com.musicUpload.dataHandler.enums.ProtectionType;
import com.musicUpload.dataHandler.models.implementations.*;
import com.musicUpload.dataHandler.repositories.AlbumRepository;
import com.musicUpload.dataHandler.repositories.UserRepository;
import com.musicUpload.dataHandler.services.AlbumService;
import com.musicUpload.dataHandler.services.MinioService;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

public class UpdateAlbumTest {
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

    private AlbumService albumService;
    private Album album;
    private Long id;
    private final ProtectionType publicProtectionType = ProtectionType.PUBLIC;
    private final ProtectionType privateProtectionType = ProtectionType.PRIVATE;
    private final UserDetailsImpl userDetails = new UserDetailsImpl(1L,
            "user1",
            "pwd",
            List.of(),
            "");
    private User user;
    private final Song song = new Song(1L,
            "",
            "foo",
            "",
            0L,
            privateProtectionType,
            new User(),
            new ArrayList<>(),
            new Date(),
            new Date());

    @BeforeEach
    void onSetUp() {
        MockitoAnnotations.initMocks(this);
        albumService = new AlbumService(albumRepository,
                                        userRepository,
                                        songService,
                                        imageFactory,
                                        minioService);
        id = 1L;
        album = new Album(id,
                "",
                "foo",
                publicProtectionType,
                new User(),
                new ArrayList<>(),
                new Date(),
                new Date());
        user = new User(1L, null, null, null,
                "user", List.of(), List.of(), Privilege.USER, List.of(), List.of(), null, null);
    }

    @Test
    void updateAlbumWithoutAuth() {
        assertThrows(UnauthenticatedException.class,
                () -> albumService.patchAlbum(null, null, null, null, null, null));
    }

    @Test
    void updateOtherUsersAlbum() {
//        userDetails.setAlbums(List.of(album));
        assertThrows(UnauthenticatedException.class,
                () -> albumService.patchAlbum(userDetails, 2L, null, null, null, null));
    }

    @Test
    void updateNameTest() {
//        userDetails.setAlbums(new ArrayList<>(List.of(album)));
        albumService.patchAlbum(
                userDetails,
                1L,
                null, null,
                "bar",
                null
        );
        assertEquals("bar", album.getName());
    }

    @Test
    void updateProtectionType() {
//        userDetails.setAlbums(new ArrayList<>(List.of(album)));
        albumService.patchAlbum(
                userDetails,
                1L,
                "PRIVATE", null,
                null,
                null
        );
        assertEquals("PRIVATE", album.getProtectionType().getName());
    }

    @Test
    void updateAlbumWithPrivateSongNotOwned() {
        song.setProtectionType(privateProtectionType);
//        userDetails.setAlbums(new ArrayList<>(List.of(album)));
        albumService.patchAlbum(userDetails,
                1L,
                null,
                List.of(1L),
                null, null);
        assertEquals(List.of(), album.getSongs());
    }

    @Test
    void updateAlbumWithPublicSongNotOwned() {
        song.setProtectionType(publicProtectionType);
//        userDetails.setAlbums(new ArrayList<>(List.of(album)));
        given(songService.findById(1L))
                .willReturn(Optional.of(song));
        albumService.patchAlbum(userDetails,
                1L,
                null,
                List.of(1L),
                null, null);
        assertEquals(List.of(song), album.getSongs());
    }

    @Test
    void updateAlbumWithPrivateSongOwned() {
        song.setProtectionType(privateProtectionType);
//        userDetails.setAlbums(new ArrayList<>(List.of(album)));
        album.setUser(user);
        user.setAlbums(List.of(album));
        user.setSongs(List.of(song));
        given(songService.findById(1L))
                .willReturn(Optional.of(song));
        albumService.patchAlbum(userDetails,
                1L,
                null,
                List.of(1L),
                null, null);
    }
}
