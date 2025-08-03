package com.musicUpload.musicUpload.recommendationEngine;

import com.musicUpload.musicUpload.recommendationEngine.database.entity.*;
import com.musicUpload.musicUpload.recommendationEngine.database.entity.Tag;
import com.musicUpload.musicUpload.recommendationEngine.database.repository.SongRepository;
import com.musicUpload.musicUpload.recommendationEngine.database.repository.TagRepository;
import com.musicUpload.musicUpload.recommendationEngine.database.repository.UserRepository;
import com.musicUpload.musicUpload.recommendationEngine.database.repository.UserSongRepository;
import com.musicUpload.musicUpload.recommendationEngine.recommendation.engine.RecommendationEngine;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = Application.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Sql(scripts = "/init-scripts/init-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/init-scripts/drop-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
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
        Tag oldTag = tagRepository.findByName("ROCK").orElseThrow();
        Tag newTag = tagRepository.findByName("POP").orElseThrow();
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
        userSongs.add(userSong1);

        UserSong userSong2 = new UserSong();
        userSong2.setUserId(user2.getId());
        userSong2.setSongId(song1.getId());
        userSongs.add(userSong2);

        UserSong userSong3 = new UserSong();
        userSong3.setUserId(user1.getId());
        userSong3.setSongId(song2.getId());
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
        userSongs.add(userSong1);

        UserSong userSong2 = new UserSong();
        userSong2.setUserId(user2.getId());
        userSong2.setSongId(song1.getId());
        userSongs.add(userSong2);

        UserSong userSong3 = new UserSong();
        userSong3.setUserId(user1.getId());
        userSong3.setSongId(song2.getId());
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
            userIds.stream().map(id -> new UserSong(1L, id)).toList()
        );

        userSongRepository.saveAll(
            songIds.stream().map(
                    id -> IntStream.range(0, id.intValue()).mapToObj(
                            userId -> new UserSong(id, (long) userId+1)
                    ).toList()
            ).flatMap(List::stream).toList()
        );

       Collections.reverse(songIds);

        assertEquals(
                songIds.stream().filter(id -> !id.equals(1L)).toList(),
                recommendationEngine.createRecommendationsForSong(1L, null)
        );
    }
}
