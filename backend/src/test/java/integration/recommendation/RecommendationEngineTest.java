package integration.recommendation;

import com.musicUpload.MusicUploadApplication;
import com.musicUpload.dataHandler.enums.Privilege;
import com.musicUpload.dataHandler.enums.ProtectionType;
import com.musicUpload.dataHandler.models.implementations.Song;
import com.musicUpload.dataHandler.models.implementations.Tag;
import com.musicUpload.dataHandler.models.implementations.User;
import com.musicUpload.dataHandler.models.implementations.UserSong;
import com.musicUpload.dataHandler.repositories.SongRepository;
import com.musicUpload.dataHandler.repositories.TagRepository;
import com.musicUpload.dataHandler.repositories.UserRepository;
import com.musicUpload.dataHandler.repositories.UserSongRepository;
import com.musicUpload.recommendation.RecommendationEngine;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = MusicUploadApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RecommendationEngineTest {

    private final RecommendationEngine recommendationEngine;
    private final UserRepository userRepository;
    private final SongRepository songRepository;
    private final TagRepository tagRepository;
    private final UserSongRepository userSongRepository;

    @Autowired
    public RecommendationEngineTest(RecommendationEngine recommendationEngine,
                                    UserRepository userRepository,
                                    SongRepository songRepository,
                                    TagRepository tagRepository,
                                    UserSongRepository userSongRepository) {
        this.recommendationEngine = recommendationEngine;
        this.userRepository = userRepository;
        this.songRepository = songRepository;
        this.tagRepository = tagRepository;
        this.userSongRepository = userSongRepository;
    }

    @BeforeAll
    public void setUpDatabase() {
        List<String> userNames = List.of(
                "user1",
                "user2",
                "user3",
                "user4",
                "user5"
        );
        List<String> firstNames = List.of(
                "user1",
                "user2",
                "user3",
                "user4",
                "user5"
        );
        List<String> lastNames = List.of(
                "user1",
                "user2",
                "user3",
                "user4",
                "user5"
        );
        List<String> emails = List.of(
                "user1@gmail.com",
                "user2@gmail.com",
                "user3@gmail.com",
                "user4@gmail.com",
                "user5@gmail.com"
        );

        List<User> users = userRepository.saveAll(
                IntStream.range(0, userNames.size()).mapToObj(i -> {
                    User user = new User();
                    user.setUsername(firstNames.get(i) + " " + lastNames.get(i));
                    user.setEmail(emails.get(i));
                    user.setPrivilege(Privilege.USER);
                    user.setPassword("password");
                    return user;
                }).toList()
        );

        List<Tag> tags = tagRepository.saveAll(
                Stream.of("ROCK", "POP", "INDIE")
                        .map(name -> {
                            Tag t = new Tag();
                            t.setName(name);
                            return t;
                        }).toList()
        );

        List<Song> songs = songRepository.saveAll(
                Stream.of(
                                "song1", "song2",
                                "song3", "song4",
                                "song5", "song6",
                                "song7", "song8",
                                "song9", "song10")
                        .map(name -> {
                            Song song = new Song();

                            song.setName(name);
                            song.setNameHashed("hashed_name");
                            song.setImage("hashed_name");
                            song.setProtectionType(ProtectionType.PUBLIC);
                            song.setListenCount(0L);
                            song.setTags(Set.of(tags.getFirst()));
                            song.setUser(users.getFirst());

                            return song;
                        }).toList()
        );
    }

    @Test
    @Order(1)
    public void testRecommendationNoListens() {
        //No inserted listens for users
        List<Long> songIds = recommendationEngine.createRecommendationsForUser(
                userRepository.findById(1L).orElseThrow().getId()
        );
        assertEquals(List.of(), songIds);
    }

    @Test
    @Order(2)
    public void testRecommendationWithNoSongsWithSameTag() {
        Song song1 = songRepository.findById(1L).orElseThrow();
        Song song2 = songRepository.findById(2L).orElseThrow();
        //we set the tag of the first song to be different from others
        Tag oldTag = tagRepository.findByNameIgnoreCase("ROCK").orElseThrow();
        Tag newTag = tagRepository.findByNameIgnoreCase("POP").orElseThrow();
        song1.setTags(Set.of(newTag));
        songRepository.save(song1);
        //we add two listens
        User user1 = userRepository.findById(1L).orElseThrow();
        User user2 = userRepository.findById(2L).orElseThrow();
        //both users listened to song1
        List<UserSong> userSongs = new ArrayList<>();

        UserSong userSong1 = new UserSong();
        userSong1.setUserId(user1.getId());
        userSong1.setSongId(song1.getId());
        userSong1.setListenCount(1L);
        userSongs.add(userSong1);

        UserSong userSong2 = new UserSong();
        userSong2.setUserId(user2.getId());
        userSong2.setSongId(song1.getId());
        userSong2.setListenCount(1L);
        userSongs.add(userSong2);

        UserSong userSong3 = new UserSong();
        userSong3.setUserId(user1.getId());
        userSong3.setSongId(song2.getId());
        userSong3.setListenCount(1L);
        userSongs.add(userSong3);

        userSongRepository.saveAll(userSongs);

        assertEquals(List.of(1L), recommendationEngine.createRecommendationsForUser(user2.getId()));

        song1.setTags(Set.of(oldTag));
        songRepository.save(song1);
        userSongRepository.deleteAll();
    }

    @Test
    @Order(3)
    public void testRecommendationWithSongsWithSameTag() {
        userSongRepository.deleteAll();
        Song song1 = songRepository.findById(1L).orElseThrow();
        Song song2 = songRepository.findById(2L).orElseThrow();
        //we add two listens
        User user1 = userRepository.findById(1L).orElseThrow();
        User user2 = userRepository.findById(2L).orElseThrow();
        //both users listened to song1
        List<UserSong> userSongs = new ArrayList<>();

        UserSong userSong1 = new UserSong();
        userSong1.setUserId(user1.getId());
        userSong1.setSongId(song1.getId());
        userSong1.setListenCount(1L);
        userSongs.add(userSong1);

        UserSong userSong2 = new UserSong();
        userSong2.setUserId(user2.getId());
        userSong2.setSongId(song1.getId());
        userSong2.setListenCount(1L);
        userSongs.add(userSong2);

        UserSong userSong3 = new UserSong();
        userSong3.setUserId(user1.getId());
        userSong3.setSongId(song2.getId());
        userSong3.setListenCount(1L);
        userSongs.add(userSong3);

        userSongRepository.saveAll(userSongs);

        assertEquals(
                List.of(1L, 2L),
                recommendationEngine.createRecommendationsForUser(user2.getId())
        );
    }

    @Test
    @Order(4)
    public void testRecommendationForSong() {
        userSongRepository.deleteAll();

        List<Long> songIds = songRepository.findAll().stream()
                .map(Song::getId)
                .limit(5)
                .collect(Collectors.toCollection(ArrayList::new));
        List<Long> userIds = userRepository.findAll().stream().map(User::getId).toList();

        userSongRepository.saveAll(
            userIds.stream().map(id -> new UserSong(1L, id, 1L)).toList()
        );

        userSongRepository.saveAll(
            songIds.stream().map(
                    id -> IntStream.range(0, id.intValue()).mapToObj(
                            userId -> new UserSong(id, (long) userId+1, 1L)
                    ).toList()
            ).flatMap(List::stream).toList()
        );

       Collections.reverse(songIds);

        assertEquals(
                songIds.stream().filter(id -> !id.equals(1L)).toList(),
                recommendationEngine.createRecommendationsForSong(1L)
        );
    }
}
