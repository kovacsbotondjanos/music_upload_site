package com.musicUpload.albumTest;

import com.musicUpload.cronJobs.EntityCacheManager;
import com.musicUpload.dataHandler.DTOs.AlbumDTO;
import com.musicUpload.dataHandler.details.CustomUserDetails;
import com.musicUpload.dataHandler.enums.ProtectionType;
import com.musicUpload.dataHandler.models.implementations.Album;
import com.musicUpload.dataHandler.models.implementations.User;
import com.musicUpload.dataHandler.repositories.AlbumRepository;
import com.musicUpload.dataHandler.repositories.UserRepository;
import com.musicUpload.dataHandler.services.AlbumService;
import com.musicUpload.dataHandler.services.SongService;
import com.musicUpload.exceptions.NotFoundException;
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

public class FindByIdTest {
    CustomUserDetails userDetails = new CustomUserDetails(1L,
            "user1",
            "pwd",
            List.of(),
            "",
            List.of(),
            List.of());
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SongService songService;
    @Mock
    private ImageFactory imageFactory;
    @Mock
    private EntityCacheManager<Album> albumEntityManager;
    private AlbumService albumService;
    private Album album;
    private Long id;
    private final ProtectionType privateprotectionType = ProtectionType.PRIVATE;

    @BeforeEach
    void onSetUp() {
        MockitoAnnotations.initMocks(this);
        albumService = new AlbumService(albumRepository,
                userRepository,
                songService,
                imageFactory,
                albumEntityManager);
        id = 1L;
        album = new Album(id,
                "",
                "foo",
                ProtectionType.PUBLIC,
                new User(),
                new ArrayList<>(),
                new Date(),
                new Date());
    }

    @Test
    void canFindByIdPublicAlbum() {
        //Given
        given(albumRepository.findById(id))
                .willReturn(Optional.of(album));
        //When
        AlbumDTO albumDTO = albumService.findById(id, null);
        //Then
        assertEquals("foo", albumDTO.getName());
    }

    @Test
    void canFindByIdNonExistingWithAuthAlbum() {
        //Given
        album.setId(2L);
        userDetails.setAlbums(List.of(album));
        given(albumRepository.findById(id))
                .willReturn(Optional.empty());
        //Then
        assertThrows(NotFoundException.class,
                () -> albumService.findById(id, userDetails));
    }

    @Test
    void canFindByIdPrivateNoAuth() {
        //Given
        album.setProtectionType(privateprotectionType);
        given(albumRepository.findById(id))
                .willReturn(Optional.of(album));
        //Then
        assertThrows(UnauthenticatedException.class,
                () -> albumService.findById(id, null));
    }

    @Test
    void canFindByIdPrivateWithUser() {
        //Given
        album.setProtectionType(privateprotectionType);
        album.setUser(new User(userDetails));
        userDetails.setAlbums(List.of(album));
        given(albumRepository.findById(id))
                .willReturn(Optional.of(album));
        //When
        AlbumDTO a = albumService.findById(id, userDetails);
        //Then
        assertEquals("foo", a.getName());
    }
}
