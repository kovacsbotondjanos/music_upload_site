package com.musicUpload.songTest;

import com.musicUpload.dataHandler.details.CustomUserDetails;
import com.musicUpload.dataHandler.models.ProtectionType;
import com.musicUpload.dataHandler.models.Song;
import com.musicUpload.dataHandler.models.User;
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

public class DeleteSongTest {
    @Mock
    private SongRepository songRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ImageFactory imageFactory;
    @Mock
    private MusicFactory songFactory;
    @Mock
    private ProtectionTypeService protectionTypeService;

    private SongService songService;
    private List<Song> songs;

    @BeforeEach
    void onSetUp(){
        MockitoAnnotations.initMocks(this);
        songService = new SongService(songRepository,
                userRepository,
                imageFactory,
                songFactory,
                protectionTypeService);
        songs = List.of(
                new Song(1L,
                        "",
                        "foo",
                        "",
                        new ProtectionType(1L, "PUBLIC", new ArrayList<>(), new ArrayList<>()),
                        new User(),
                        new ArrayList<>(),
                        new Date(),
                        new Date()),
                new Song(2L,
                        "",
                        "bar",
                        "",
                        new ProtectionType(1L, "PUBLIC", new ArrayList<>(), new ArrayList<>()),
                        new User(),
                        new ArrayList<>(),
                        new Date(),
                        new Date()),
                new Song(3L,
                        "",
                        "baz",
                        "",
                        new ProtectionType(1L, "PUBLIC", new ArrayList<>(), new ArrayList<>()),
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
        CustomUserDetails userDetails = new CustomUserDetails(1L,
                "user1",
                "",
                List.of(),
                "",
                List.of(songs.get(0)),
                List.of());
        assertThrows(UnauthenticatedException.class,
                () -> songService.deleteSong(userDetails, 2L));
    }

    @Test
    void canDeleteOwnSongWithAuth(){
        CustomUserDetails userDetails = new CustomUserDetails(1L,
                "user1",
                "",
                List.of(),
                "",
                new ArrayList<>(List.of(songs.get(0))),
                List.of());
        Song s = songService.deleteSong(userDetails, 1L);
        assertEquals(songs.get(0), s);
        assertFalse(userDetails.getSongs().contains(s));
    }
}
