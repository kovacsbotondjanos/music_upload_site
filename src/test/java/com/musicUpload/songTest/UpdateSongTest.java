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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    private ImageFactory imageFactory;
    @Mock
    private MusicFactory songFactory;
    @Mock
    private ProtectionTypeService protectionTypeService;

    private SongService songService;
    private Song song;
    private Long id;
    private CustomUserDetails userDetails;

    @BeforeEach
    void onSetUp(){
        MockitoAnnotations.initMocks(this);
        songService = new SongService(songRepository,
                userRepository,
                imageFactory,
                songFactory,
                protectionTypeService);
        id = 1L;
        song = new Song(id,
                "",
                "foo",
                "",
                new ProtectionType(1L, "PUBLIC", new ArrayList<>(), new ArrayList<>()),
                new User(),
                new ArrayList<>(),
                new Date(),
                new Date());
        userDetails = new CustomUserDetails(
                1L,
                "",
                "",
                new ArrayList<>(),
                "",
                List.of(song),
                new ArrayList<>());
    }

    @Test
    void updateSongWithoutAuth(){
        assertThrows(UnauthenticatedException.class,
                () -> songService.updateSong(
                        null,
                        1L,
                        "",
                        "",
                        null));
    }

    @Test
    void updateOtherUsersSong(){
        assertThrows(UnauthenticatedException.class,
                () -> songService.updateSong(
                        userDetails,
                        2L,
                        "",
                        "",
                        null));
    }

    @Test
    void updateNameTest(){
        songService.updateSong(
                userDetails,
                1L,
                null,
                "bar",
                null);

        assertEquals("bar", song.getName());
    }

    @Test
    void updateProtectionTest(){
        given(protectionTypeService.getProtectionTypeByName("PROTECTED"))
                .willReturn(Optional.of(new ProtectionType(1L, "PROTECTED", null, null)));
        songService.updateSong(
                userDetails,
                1L,
                "PROTECTED",
                null,
                null);

        assertEquals("PROTECTED", song.getProtectionType().getName());
    }
}
