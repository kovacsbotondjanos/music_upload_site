package com.musicUpload.songTest;

import com.musicUpload.cronJobs.SongCacheManager;
import com.musicUpload.dataHandler.DTOs.SongDTO;
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
import com.musicUpload.exceptions.NotFoundException;
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

public class FindByIdTest {
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
    private Song song;
    private Long id;
    private final ProtectionType privateprotectionType = ProtectionType.PRIVATE;
    private final UserDetailsImpl userDetails = new UserDetailsImpl(1L,
            "user1",
            "pwd",
            List.of(),
            "");

    @BeforeEach
    void onSetUp() {
        MockitoAnnotations.initMocks(this);
        songService = new SongService(songRepository,
                                      userRepository,
                                      albumRepository,
                                      imageFactory,
                                      userRecommendationService,
                                      minioService);
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
    }

    @Test
    void canFindByIdPublicSongNoAuth() {
        //Given
        given(songRepository.findById(id))
                .willReturn(Optional.of(song));
        //When
        SongDTO actualSong = songService.findById(null, id);
        //Then
        assertEquals("foo", actualSong.getName());
    }

    @Test
    void canFindByIdNonExistingSongWithAuth() {
        //Given
        song.setId(2L);
        song.setUser(new User(userDetails));
//        userDetails.setSongs(List.of(song));
        given(songRepository.findById(id))
                .willReturn(Optional.empty());
        //Then
        assertThrows(NotFoundException.class,
                () -> songService.findById(userDetails, id));
    }

    @Test
    void canFindByIdPrivateNoAuth() {
        //Given
        song.setProtectionType(privateprotectionType);
        given(songRepository.findById(id))
                .willReturn(Optional.of(song));
        //Then
        assertThrows(UnauthenticatedException.class,
                () -> songService.findById(null, id));
    }

    @Test
    void canFindByIdPrivateWithUser() {
        //Given
        song.setProtectionType(privateprotectionType);
        song.setUser(new User(userDetails));
//        userDetails.setSongs(List.of(song));
        given(songRepository.findById(id))
                .willReturn(Optional.of(song));
        //When
        SongDTO s = songService.findById(userDetails, id);
        //Then
        assertEquals("foo", s.getName());
    }
}
