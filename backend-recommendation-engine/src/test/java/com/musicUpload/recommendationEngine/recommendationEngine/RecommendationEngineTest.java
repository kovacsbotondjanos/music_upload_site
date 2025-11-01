package com.musicUpload.recommendationEngine.recommendationEngine;

import com.musicUpload.musicUpload.recommendationEngine.Application;
import com.musicUpload.musicUpload.recommendationEngine.database.entity.*;
import com.musicUpload.musicUpload.recommendationEngine.database.entity.Tag;
import com.musicUpload.musicUpload.recommendationEngine.database.repository.SongRepository;
import com.musicUpload.musicUpload.recommendationEngine.database.repository.TagRepository;
import com.musicUpload.musicUpload.recommendationEngine.database.repository.UserRepository;
import com.musicUpload.musicUpload.recommendationEngine.database.repository.UserSongRepository;
import com.musicUpload.musicUpload.recommendationEngine.database.service.RecommendationEngine;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
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
                userRepository.findById(1L).orElseThrow().getId(), 20L, 0L
        );
        // We expect the only two songs to be returned that has listens
        assertEquals(List.of(4L, 3L), songIds);
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
        userSong3.setUserId(user2.getId());
        userSong3.setSongId(song2.getId());
        userSongs.add(userSong3);

        userSongRepository.saveAll(userSongs);

        assertEquals(List.of(1L, 4L, 3L), recommendationEngine.createRecommendationsForUser(user2.getId(), 20L, 0L));

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
        Song song5 = songRepository.findById(5L).orElseThrow();
        Song song6 = songRepository.findById(6L).orElseThrow();
        //we add two listens
        User user1 = userRepository.findById(1L).orElseThrow();
        User user2 = userRepository.findById(2L).orElseThrow();
        User user3 = userRepository.findById(3L).orElseThrow();
        User user4 = userRepository.findById(4L).orElseThrow();
        User user7 = userRepository.findById(7L).orElseThrow();
        //both users listened to song1
        List<UserSong> userSongs = new ArrayList<>();

        // user 1
        UserSong userSong11 = new UserSong();
        userSong11.setUserId(user1.getId());
        userSong11.setSongId(song1.getId());
        userSongs.add(userSong11);

        // user 2
        UserSong userSong21 = new UserSong();
        userSong21.setUserId(user2.getId());
        userSong21.setSongId(song1.getId());
        userSongs.add(userSong21);

        UserSong userSong22 = new UserSong();
        userSong22.setUserId(user2.getId());
        userSong22.setSongId(song2.getId());
        userSongs.add(userSong22);

        UserSong userSong25 = new UserSong();
        userSong25.setUserId(user2.getId());
        userSong25.setSongId(song5.getId());
        userSongs.add(userSong25);

        UserSong userSong26 = new UserSong();
        userSong26.setUserId(user2.getId());
        userSong26.setSongId(song6.getId());
        userSongs.add(userSong26);

        // user 3
        UserSong userSong31 = new UserSong();
        userSong31.setUserId(user3.getId());
        userSong31.setSongId(song1.getId());
        userSongs.add(userSong31);

        UserSong userSong35 = new UserSong();
        userSong35.setUserId(user3.getId());
        userSong35.setSongId(song5.getId());
        userSongs.add(userSong35);

        UserSong userSong36 = new UserSong();
        userSong36.setUserId(user3.getId());
        userSong36.setSongId(song6.getId());
        userSongs.add(userSong36);

        // user 4
        UserSong userSong41 = new UserSong();
        userSong41.setUserId(user4.getId());
        userSong41.setSongId(song1.getId());
        userSongs.add(userSong41);

        UserSong userSong46 = new UserSong();
        userSong46.setUserId(user4.getId());
        userSong46.setSongId(song6.getId());
        userSongs.add(userSong46);

        // user 7
        UserSong userSong71 = new UserSong();
        userSong71.setUserId(user7.getId());
        userSong71.setSongId(song1.getId());
        userSongs.add(userSong71);

        userSongRepository.saveAll(userSongs);

        /*
         1L => 4 listens
         6L => 3 listens
         5L => 2 listens
         then deafult: 4L (200 listens total), 3L (100 listens total)
        */
        assertEquals(
                List.of(1L, 6L, 5L, 4L, 3L),
                recommendationEngine.createRecommendationsForUser(user2.getId(), 20L, 0L)
        );
    }

    @Test
    @Order(4)
    public void testRecommendationForSong() {
        userSongRepository.deleteAll();

        List<Long> songIds = songRepository.findAll().stream()
                .map(Song::getId)
                .limit(5)
                .toList();
        List<Long> userIds = userRepository.findAll().stream().map(User::getId).toList();

        userSongRepository.saveAll(
            userIds.stream().map(id -> new UserSong(1L, id)).toList()
        );

        List<UserSong> listens = songIds.stream().map(
                id -> IntStream.range(0, id.intValue()).mapToObj(
                        userId -> new UserSong(id, (long) userId+1)
                ).toList()
        ).flatMap(List::stream).toList();

        userSongRepository.saveAll(listens);

        assertEquals(
                List.of(5L, 4L, 3L, 2L),
                recommendationEngine.createRecommendationsForSong(1L, null, 20L, 0L)
        );
    }

    @Test
    @Order(5)
    public void testRecommendationForUserFallbackToDefault() {
        userSongRepository.deleteAll();

        userSongRepository.saveAll(
                List.of(
                        UserSong.builder()
                                .songId(1L)
                                .userId(1L)
                                .build(),
                        UserSong.builder()
                                .songId(1L)
                                .userId(2L)
                                .build(),
                        UserSong.builder()
                                .songId(2L)
                                .userId(2L)
                                .build()
                )
        );

        Assertions.assertEquals(
                List.of(1L, 2L, 4L, 3L),
                recommendationEngine.createRecommendationsForUser(1L, 20L, 0L)
        );
    }
}
