package com.musicUpload.cronJobs;

import com.musicUpload.dataHandler.details.UserDetailsImpl;
import com.musicUpload.dataHandler.models.implementations.UserSong;
import com.musicUpload.dataHandler.repositories.SongRepository;
import com.musicUpload.dataHandler.repositories.UserSongRepository;
import com.musicUpload.util.Pair;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SongListenCountUpdateScheduler {
    private final SongRepository songRepository;
    private final UserSongRepository userSongRepository;
    private final int SCHEDULE = 2 * 1000 * 60;
    private Map<Pair<Long, Long>, Long> songListensBuffer = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final ExecutorService listenCountExecutor = Executors.newFixedThreadPool(5);

    public void addListenToSong(Long songId, UserDetailsImpl userDetails) {
        listenCountExecutor.submit(() -> {
            Long userId = userDetails == null ? null : userDetails.getId();
            songListensBuffer.merge(Pair.of(songId, userId), 1L, Long::sum);
            log.debug("buffer: {}", songListensBuffer);
        });
    }

    // destroy the executorService
    @PreDestroy
    public void shutdownExecutor() {
        executor.shutdown();
        listenCountExecutor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            listenCountExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        try {
            if (!listenCountExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                listenCountExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            listenCountExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    @Scheduled(fixedRate = SCHEDULE)
    @SchedulerLock(name = "SongListenCountUpdateScheduler_saveReport", lockAtMostFor = "2m")
    public void saveReport() {
        Map<Pair<Long, Long>, Long> copyMap;
        synchronized (SongListenCountUpdateScheduler.class) {
            copyMap = songListensBuffer;
            songListensBuffer = new ConcurrentHashMap<>();
        }
        log.debug("{} new listens are being saved into the database", copyMap.size());
        executor.submit(() -> {
            Map<Long, Long> songsToSave = new ConcurrentHashMap<>();
            Queue<UserSong> userListensToSave = new ConcurrentLinkedQueue<>();
            //this is not optimized, i'll have to look into this in the future, but this is the best i came up for now
            copyMap.forEach((e, v) -> {
                songsToSave.merge(e.getFirst(), v, Long::sum);

                if (e.getSecond() != null) {
                    userListensToSave.add(
                            UserSong.builder()
                                    .songId(e.getFirst())
                                    .userId(e.getSecond())
                                    .build()
                    );
                }
            });

            songsToSave.forEach(songRepository::updateSong);

            userSongRepository.saveAll(userListensToSave);

            copyMap.clear();
        });
    }
}
