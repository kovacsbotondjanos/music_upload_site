package com.musicUpload.songTest;

import com.musicUpload.cronJobs.EntityManager;
import com.musicUpload.cronJobs.SongListenCountJob;
import com.musicUpload.dataHandler.details.CustomUserDetails;
import com.musicUpload.dataHandler.models.implementations.ProtectionType;
import com.musicUpload.dataHandler.models.implementations.Song;
import com.musicUpload.dataHandler.models.implementations.User;
import com.musicUpload.dataHandler.repositories.AlbumRepository;
import com.musicUpload.dataHandler.repositories.SongRepository;
import com.musicUpload.dataHandler.repositories.UserRepository;
import com.musicUpload.dataHandler.services.ProtectionTypeService;
import com.musicUpload.dataHandler.services.SongService;
import com.musicUpload.exceptions.UnauthenticatedException;
import com.musicUpload.util.ImageFactory;
import com.musicUpload.util.MusicFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

public class DeleteSongTest {
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
    private ProtectionTypeService protectionTypeService;
    @Mock
    private SongListenCountJob listenCountJob;
    @Mock
    private EntityManager<Song> entityManager;

    private SongService songService;
    private List<Song> songs;
    private ProtectionType protectionType = new ProtectionType(1L, "PUBLIC", new ArrayList<>(), new ArrayList<>());
    CustomUserDetails userDetails = new CustomUserDetails(1L,
            "user1",
            "",
            List.of(),
            "",
            List.of(),
            List.of());

    @BeforeEach
    void onSetUp(){
        MockitoAnnotations.initMocks(this);
        songService = new SongService(songRepository,
                userRepository,
                albumRepository,
                imageFactory,
                songFactory,
                protectionTypeService,
                listenCountJob,
                entityManager);
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
    void canDeleteWithoutAuth(){
        assertThrows(UnauthenticatedException.class,
                () -> songService.deleteSong(null, 1L));
    }

    @Test
    void canDeleteOtherUserSongWithAuth(){
        //Given
        userDetails.setSongs(List.of(songs.get(0)));
        given(userRepository.findById(userDetails.getId()))
                .willReturn(Optional.of(new User(userDetails)));
        //Then
        assertThrows(UnauthenticatedException.class,
                () -> songService.deleteSong(userDetails, 2L));
    }

    @Test
    void canDeleteOwnSongWithAuth(){
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
