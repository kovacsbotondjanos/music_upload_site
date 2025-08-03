package com.musicUpload.musicUpload.benchmark;


import com.musicUpload.musicUpload.recommendationEngine.Application;
import com.musicUpload.musicUpload.recommendationEngine.database.entity.Song;
import com.musicUpload.musicUpload.recommendationEngine.database.entity.User;
import com.musicUpload.musicUpload.recommendationEngine.database.entity.UserSong;
import com.musicUpload.musicUpload.recommendationEngine.database.repository.SongRepository;
import com.musicUpload.musicUpload.recommendationEngine.database.repository.TagRepository;
import com.musicUpload.musicUpload.recommendationEngine.database.repository.UserRepository;
import com.musicUpload.musicUpload.recommendationEngine.database.repository.UserSongRepository;
import com.musicUpload.musicUpload.recommendationEngine.recommendation.engine.RecommendationEngine;
import lombok.extern.slf4j.Slf4j;
import org.databene.contiperf.PerfTest;
import org.databene.contiperf.Required;
import org.databene.contiperf.junit.JUnitPerfTestFailure;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = Application.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
public class RecommendationEnginePerformanceTest {

    private final RecommendationEngine recommendationEngine;
    private final UserRepository userRepository;
    private final SongRepository songRepository;
    private final UserSongRepository userSongRepository;

    @Autowired
    public RecommendationEnginePerformanceTest(RecommendationEngine recommendationEngine,
                                    UserRepository userRepository,
                                    SongRepository songRepository,
                                    UserSongRepository userSongRepository) {
        this.recommendationEngine = recommendationEngine;
        this.userRepository = userRepository;
        this.songRepository = songRepository;
        this.userSongRepository = userSongRepository;
    }

    @Test
    @PerfTest(invocations = 10)
    @Required(average = 10)
    @Sql(scripts = "/init-scripts/init-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/init-scripts/drop-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void benchMarkEasyQuery() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("recommend");

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

        recommendationEngine.createRecommendationsForUser(user2.getId());
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());
    }
}
