package unit.songTest;

import com.musicUpload.dataHandler.details.UserDetailsImpl;
import com.musicUpload.dataHandler.enums.ProtectionType;
import com.musicUpload.dataHandler.models.implementations.Song;
import com.musicUpload.dataHandler.models.implementations.User;
import com.musicUpload.dataHandler.repositories.SongRepository;
import com.musicUpload.dataHandler.repositories.UserRepository;
import com.musicUpload.dataHandler.services.MinioService;
import com.musicUpload.dataHandler.services.SongService;
import com.musicUpload.exceptions.UnauthenticatedException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

public class DeleteSongTest {
    private final ProtectionType protectionType = ProtectionType.PUBLIC;
    UserDetailsImpl userDetails = new UserDetailsImpl(
            1L,
            "user1",
            "",
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
    private MinioService minioService;
    @InjectMocks
    private SongService songService;
    private List<Song> songs;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void onSetUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        songs = List.of(
                createSong(1L, "foo"),
                createSong(2L, "bar"),
                createSong(3L, "baz")
        );
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @AfterEach
    void closeMocks() throws Exception {
        autoCloseable.close();
    }


    @Test
    void canDeleteWithoutAuth() {
        assertThrows(UnauthenticatedException.class,
                () -> songService.deleteSong(1L));
    }

    @Test
    void canDeleteOtherUserSongWithAuth() {
        //Given
        User u = new User(userDetails);
        SecurityContextHolder.setContext(securityContext);
        given(userRepository.findById(userDetails.getId()))
                .willReturn(Optional.of(u));
        given(songRepository.findByUserAndId(u, 2L))
                .willReturn(Optional.empty());
        //Then
        assertThrows(UnauthenticatedException.class,
                () -> songService.deleteSong(2L));
        SecurityContextHolder.clearContext();
    }

    @Test
    void canDeleteOwnSongWithAuth() {
        //Given
        User u = new User(userDetails);
        SecurityContextHolder.setContext(securityContext);
        given(userRepository.findById(userDetails.getId()))
                .willReturn(Optional.of(u));
        given(songRepository.findByUserAndId(u, 1L))
                .willReturn(Optional.of(songs.getFirst()));
        //Then
        Song s = songService.deleteSong(1L);
        assertEquals(songs.getFirst(), s);
        assertFalse(u.getSongs().contains(s));
        SecurityContextHolder.clearContext();
    }

    private Song createSong(Long id, String name) {
        Date now = new Date();
        return Song.builder()
                .id(id)
                .image("")
                .name(name)
                .nameHashed("")
                .listenCount(1L)
                .protectionType(protectionType)
                .user(new User())
                .albums(new ArrayList<>())
                .tags(new HashSet<>())
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
