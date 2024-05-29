package com.musicUpload.songTest;

import com.musicUpload.dataHandler.DTOs.SongDTO;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FindByIdTest {
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
    private ProtectionType privateprotectionType = new ProtectionType(1L, "PRIVATE", List.of(), List.of());
    private CustomUserDetails userDetails = new CustomUserDetails(1L,
                                                                "user1",
                                                                "pwd",
                                                                List.of(),
                                                                "",
                                                                List.of(),
                                                                List.of());

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
    }

    @Test
    void canFindByIdPublicSongNoAuth(){
        //Given
        given(songRepository.findById(id))
                .willReturn(Optional.of(song));
        //When
        SongDTO actualSong = songService.findById(null, id);
        //Then
        assertEquals("foo", actualSong.getName());
    }

    @Test
    void canFindByIdNonExistingSongWithAuth(){
        //Given
        song.setId(2L);
        userDetails.setSongs(List.of(song));
        given(songRepository.findById(id))
                .willReturn(Optional.empty());
        //Then
        assertThrows(UnauthenticatedException.class,
                () -> songService.findById(userDetails, id));
    }

    @Test
    void canFindByIdPrivateNoAuth(){
        //Given
        song.setProtectionType(privateprotectionType);
        given(songRepository.findById(id))
                .willReturn(Optional.of(song));
        //Then
        assertThrows(UnauthenticatedException.class,
                () -> songService.findById(null, id));
    }

    @Test
    void canFindByIdPrivateWithUser(){
        //Given
        song.setProtectionType(privateprotectionType);
        userDetails.setSongs(List.of(song));
        given(songRepository.findById(id))
                .willReturn(Optional.of(song));
        //When
        SongDTO s = songService.findById(userDetails, id);
        //Then
        assertEquals("foo", s.getName());
    }
}
