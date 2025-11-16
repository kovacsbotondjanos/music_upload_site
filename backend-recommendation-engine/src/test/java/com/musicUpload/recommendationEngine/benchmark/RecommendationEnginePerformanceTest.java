package com.musicUpload.recommendationEngine.benchmark;


import com.musicUpload.musicUpload.recommendationEngine.Application;
import com.musicUpload.musicUpload.recommendationEngine.database.service.RecommendationEngine;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.util.StopWatch;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringBootTest(classes = Application.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
public class RecommendationEnginePerformanceTest {

    private final RecommendationEngine recommendationEngine;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RecommendationEnginePerformanceTest(RecommendationEngine recommendationEngine, JdbcTemplate jdbcTemplate) {
        this.recommendationEngine = recommendationEngine;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Test
    @Sql(scripts = "/init-scripts/performance-test-data/init-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/init-scripts/drop-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void benchMarkEasyQuery_1() {
        createData(10);
        benchmarkTest(1000L, 1000L);
    }

    @Test
    @Sql(scripts = "/init-scripts/performance-test-data/init-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/init-scripts/drop-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void benchMarkMediumQuery_1() {
        createData(500);
        benchmarkTest(1000L, 1000L);
    }

    @Test
    @Sql(scripts = "/init-scripts/performance-test-data/init-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/init-scripts/drop-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void benchMarkHardQuery_1() {
        createData(10_000);
        benchmarkTest(2000L, 2000L);
    }

    @Test
    @Sql(scripts = "/init-scripts/performance-test-data/init-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/init-scripts/drop-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void benchMarkHardQuery_2() {
        createData(100_000);
        benchmarkTest(2000L, 2000L);
    }

    @Test
    @Sql(scripts = "/init-scripts/performance-test-data/init-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/init-scripts/drop-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void benchMarkHardQuery_3() {
        createData(250_000);
        benchmarkTest(2000L, 2000L);
    }

    @Test
    @Sql(scripts = "/init-scripts/performance-test-data/init-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/init-scripts/drop-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void benchMarkHardQuery_4() {
        createData(500_000);
        benchmarkTest(2000L, 2000L);
    }

    private void benchmarkTest(Long userRecommendationMillis, Long songRecommendationMillis) {
        // the first test was always slow, so i added this warmup call here to eliminate the difference
        recommendationEngine.createRecommendationsForUser(0L, 20L, 0L);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start("recommend-user");

        recommendationEngine.createRecommendationsForUser(4L, 20L, 0L);

        stopWatch.stop();
        log.info(stopWatch.prettyPrint());
        Assertions.assertTrue(Arrays.stream(stopWatch.getTaskInfo())
                .map(StopWatch.TaskInfo::getTimeMillis)
                .allMatch(millis -> millis < userRecommendationMillis));

        stopWatch = new StopWatch();
        stopWatch.start("recommend-song");

        recommendationEngine.createRecommendationsForSong(8L, 4L, 20L, 0L);

        stopWatch.stop();
        log.info(stopWatch.prettyPrint());
        Assertions.assertTrue(Arrays.stream(stopWatch.getTaskInfo())
                .map(StopWatch.TaskInfo::getTimeMillis)
                .allMatch(millis -> millis < songRecommendationMillis));
    }

    private void createData(int listenRowCount) {
        createData();
        setUpDate(listenRowCount);
    }

    private void setUpDate(int listenRowCount) {
        jdbcTemplate.execute(String.format("UPDATE user_song SET created_at = NOW() LIMIT %d", listenRowCount));
    }

    private void createData() {
        Random rand = new Random();
        String data = IntStream.rangeClosed(1, 500_000).mapToObj(i ->
                String.format("(%d, %d, '2000-01-01 00:00:00', NOW())", rand.nextLong(30L), rand.nextLong(30L)))
                .collect(Collectors.joining(", "));
        jdbcTemplate.execute(
                String.format("INSERT INTO user_song (user_id, song_id, created_at, updated_at) VALUES %s;", data)
        );
    }
}
