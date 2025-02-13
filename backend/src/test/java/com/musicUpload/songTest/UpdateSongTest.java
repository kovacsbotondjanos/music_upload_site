package com.musicUpload.songTest;

import com.musicUpload.dataHandler.details.UserDetailsImpl;
import com.musicUpload.dataHandler.enums.ProtectionType;
import com.musicUpload.dataHandler.models.implementations.Song;
import com.musicUpload.dataHandler.models.implementations.User;
import com.musicUpload.dataHandler.repositories.AlbumRepository;
import com.musicUpload.dataHandler.repositories.SongRepository;
import com.musicUpload.dataHandler.repositories.UserRepository;
import com.musicUpload.dataHandler.services.MinioService;
import com.musicUpload.dataHandler.services.SongService;
import com.musicUpload.dataHandler.services.UserRecommendationService;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

public class UpdateSongTest {
    @Mock
    private SongRepository songRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private ImageFactory imageFactory;
    @Mock
    private UserRecommendationService userRecommendationService;
    @Mock
    private MinioService minioService;
    @InjectMocks
    private SongService songService;
    private Song song;
    private Long id;
    private UserDetailsImpl userDetails;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void onSetUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        id = 1L;
        song = new Song(id,
                "",
                "foo",
                "",
                1L,
                ProtectionType.PUBLIC,
                new User(),
                new ArrayList<>(),
                new Date(),
                new Date());
        userDetails = new UserDetailsImpl(
                1L,
                "",
                "",
                new ArrayList<>(),
                "");
    }

    @AfterEach
    void closeMocks() throws Exception {
        autoCloseable.close();
    }

    @Test
    void updateSongWithoutAuth() {
        assertThrows(UnauthenticatedException.class,
                () -> songService.patchSong(
                        null,
                        1L,
                        "",
                        "",
                        null
                )
        );
    }

    @Test
    void updateOtherUsersSong() {
        User u = new User(userDetails);
        given(userRepository.findById(userDetails.getId()))
                .willReturn(Optional.of(u));
        given(songRepository.findByUserAndId(u, 2L))
                .willReturn(Optional.empty());
        assertThrows(UnauthenticatedException.class,
                () -> songService.patchSong(
                        userDetails,
                        2L,
                        "",
                        "",
                        null));
    }

    @Test
    void updateNameTest() {
        User u = new User(userDetails);
        given(userRepository.findById(userDetails.getId()))
                .willReturn(Optional.of(u));
        given(songRepository.findByUserAndId(u, 1L))
                .willReturn(Optional.of(song));
        songService.patchSong(userDetails,
                1L,
                null,
                "bar",
                null);

        assertEquals("bar", song.getName());
    }

    @Test
    void updateProtectionTest() {
        User u = new User(userDetails);
        given(userRepository.findById(userDetails.getId()))
                .willReturn(Optional.of(u));
        given(songRepository.findByUserAndId(u, 1L))
                .willReturn(Optional.of(song));
        songService.patchSong(userDetails,
                1L,
                "PROTECTED",
                null,
                null);

        assertEquals("PROTECTED", song.getProtectionType().getName());
    }
}
