package unit.songTest;

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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

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
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;
    @Mock
    private UserDetailsImpl userDetails;
    @InjectMocks
    private SongService songService;
    private Song song;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void onSetUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        song = Song.builder()
                .id(1L)
                .image("")
                .name("foo")
                .nameHashed("")
                .listenCount(1L)
                .protectionType(ProtectionType.PUBLIC)
                .user(new User())
                .albums(new ArrayList<>())
                .tags(new HashSet<>())
                .createdAt(new Date())
                .updatedAt(new Date())
                .build();
        userDetails = new UserDetailsImpl(
                1L,
                "",
                "",
                new ArrayList<>(),
                ""
        );
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @AfterEach
    void closeMocks() throws Exception {
        autoCloseable.close();
    }

    @Test
    void updateSongWithoutAuth() {
        assertThrows(UnauthenticatedException.class,
                () -> songService.patchSong(
                        1L,
                        "",
                        "",
                        new ArrayList<>(),
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
                        2L,
                        "",
                        "",
                        new ArrayList<>(),
                        null)
        );
    }

    @Test
    void updateNameTest() {
        User u = new User(userDetails);
        given(userRepository.findById(userDetails.getId()))
                .willReturn(Optional.of(u));
        given(songRepository.findByUserAndId(u, 1L))
                .willReturn(Optional.of(song));
        songService.patchSong(
                1L,
                null,
                "bar",
                new ArrayList<>(),
                null
        );

        assertEquals("bar", song.getName());
    }

    @Test
    void updateProtectionTest() {
        User u = new User(userDetails);
        given(userRepository.findById(userDetails.getId()))
                .willReturn(Optional.of(u));
        given(songRepository.findByUserAndId(u, 1L))
                .willReturn(Optional.of(song));
        songService.patchSong(
                1L,
                "PROTECTED",
                null,
                new ArrayList<>(),
                null
        );

        assertEquals("PROTECTED", song.getProtectionType().getName());
    }
}
