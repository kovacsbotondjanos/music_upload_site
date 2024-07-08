package com.musicUpload.songTest;

import com.musicUpload.cronJobs.SongCacheManager;
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
import com.musicUpload.util.MusicFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

public class DeleteSongTest {
    UserDetailsImpl userDetails = new UserDetailsImpl(1L,
            "user1",
            "",
            List.of(),
            "",
            List.of(),
            List.of());
    @Mock
    private SongRepository songRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private ImageFactory imageFactory;
    @Mock
    private MusicFactory songFactory;
    @Mock
    private SongCacheManager listenCountJob;
    @Mock
    private UserRecommendationService userRecommendationService;
    @Mock
    private MinioService minioService;

    private SongService songService;
    private List<Song> songs;
    private final ProtectionType protectionType = ProtectionType.PUBLIC;

    @BeforeEach
    void onSetUp() {
        MockitoAnnotations.initMocks(this);
        songService = new SongService(songRepository,
                                      userRepository,
                                      albumRepository,
                                      imageFactory,
                                      songFactory,
                                      listenCountJob,
                                      userRecommendationService,
                                      minioService);
        songs = List.of(
                new Song(1L,
                        "",
                        "foo",
                        "",
                        1L,
                        protectionType,
                        new User(),
                        new ArrayList<>(),
                        new Date(),
                        new Date()),
                new Song(2L,
                        "",
                        "bar",
                        "",
                        1L,
                        protectionType,
                        new User(),
                        new ArrayList<>(),
                        new Date(),
                        new Date()),
                new Song(3L,
                        "",
                        "baz",
                        "",
                        1L,
                        protectionType,
                        new User(),
                        new ArrayList<>(),
                        new Date(),
                        new Date()));
    }

    @Test
    void canDeleteWithoutAuth() {
        assertThrows(UnauthenticatedException.class,
                () -> songService.deleteSong(null, 1L));
    }

    @Test
    void canDeleteOtherUserSongWithAuth() {
        //Given
        userDetails.setSongs(List.of(songs.get(0)));
        given(userRepository.findById(userDetails.getId()))
                .willReturn(Optional.of(new User(userDetails)));
        //Then
        assertThrows(UnauthenticatedException.class,
                () -> songService.deleteSong(userDetails, 2L));
    }

    @Test
    void canDeleteOwnSongWithAuth() {
        //Given
        userDetails.setSongs(new ArrayList<>(List.of(songs.get(0))));
        given(userRepository.findById(userDetails.getId()))
                .willReturn(Optional.of(new User(userDetails)));
        //Then
        Song s = songService.deleteSong(userDetails, 1L);
        assertEquals(songs.get(0), s);
        assertFalse(userDetails.getSongs().contains(s));
    }
}
