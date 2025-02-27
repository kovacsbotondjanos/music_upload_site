package unit.songTest;

import com.musicUpload.cronJobs.SongListenCountUpdateScheduler;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

public class FindByIdTest {
    private final ProtectionType privateprotectionType = ProtectionType.PRIVATE;
    @Mock
    private final UserDetailsImpl userDetails = new UserDetailsImpl(
            1L,
            "user1",
            "pwd",
            List.of(),
            ""
    );
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;
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
    private SongListenCountUpdateScheduler listenCountJob;
    @Mock
    private UserRecommendationService userRecommendationService;
    @Mock
    private MinioService minioService;
    @InjectMocks
    private SongService songService;
    private Song song;
    private Long id;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void onSetUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        id = 1L;
        song = new Song(
                id,
                "",
                "foo",
                "",
                1L,
                ProtectionType.PUBLIC,
                new User(),
                new ArrayList<>(),
                new HashSet<>(),
                new Date(),
                new Date()
        );
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @AfterEach
    void closeMocks() throws Exception {
        autoCloseable.close();
    }

    @Test
    void canFindByIdPublicSongNoAuth() {
        //Given
        given(songRepository.findById(id))
                .willReturn(Optional.of(song));
        //When
        SongDTO actualSong = songService.findById(id);
        //Then
        assertEquals("foo", actualSong.getName());
    }

    @Test
    void canFindByIdNonExistingSongWithAuth() {
        //Given
        song.setId(2L);
        song.setUser(new User(userDetails));
        SecurityContextHolder.setContext(securityContext);
        given(songRepository.findById(id))
                .willReturn(Optional.empty());
        //Then
        assertThrows(NotFoundException.class,
                () -> songService.findById(id));
        SecurityContextHolder.clearContext();
    }

    @Test
    void canFindByIdPrivateNoAuth() {
        //Given
        song.setProtectionType(privateprotectionType);
//        song.setUser(new User(userDetails));
        SecurityContextHolder.clearContext();
        when(securityContext.getAuthentication()).thenReturn(null);
        given(songRepository.findById(id))
                .willReturn(Optional.of(song));
        //Then
        assertThrows(UnauthenticatedException.class,
                () -> songService.findById(id));
    }

    @Test
    void canFindByIdPrivateWithUser() {
        //Given
        song.setProtectionType(privateprotectionType);
        song.setUser(new User(userDetails));
        SecurityContextHolder.setContext(securityContext);
        given(songRepository.findById(id))
                .willReturn(Optional.of(song));
        //When
        SongDTO s = songService.findById(id);
        //Then
        assertEquals("foo", s.getName());
        SecurityContextHolder.clearContext();
    }
}
