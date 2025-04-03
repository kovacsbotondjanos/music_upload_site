package com.musicUpload.cronJobs;

import com.musicUpload.dataHandler.details.UserDetailsImpl;
import com.musicUpload.dataHandler.models.implementations.UserSong;
import com.musicUpload.dataHandler.repositories.SongRepository;
import com.musicUpload.dataHandler.repositories.UserSongRepository;
import com.musicUpload.util.Pair;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class SongListenCountUpdateScheduler {
    private final SongRepository songRepository;
    private final UserSongRepository userSongRepository;
    private final int SCHEDULE = 2 * 1000 * 60;
    private Set<Pair<Pair<Long, Long>, Date>> songListensBuffer;

    @Autowired
    public SongListenCountUpdateScheduler(SongRepository songRepository,
                                          UserSongRepository userSongRepository) {
        this.songRepository = songRepository;
        this.userSongRepository = userSongRepository;
        this.songListensBuffer = Collections.synchronizedSet(new HashSet<>());
    }

    public void addListenToSong(Long songId, UserDetailsImpl userDetails) {
        new Thread(() -> {
            Long userId = userDetails == null ? null : userDetails.getId();
            songListensBuffer.add(Pair.of(Pair.of(songId, userId), new Date()));
            log.info("buffer: {}", songListensBuffer);
        }).start();
    }

    @Scheduled(fixedRate = SCHEDULE)
    @SchedulerLock(name = "SongListenCountUpdateScheduler_saveReport", lockAtMostFor = "2m")
    public void saveReport() {
        Set<Pair<Pair<Long, Long>, Date>> copyMap;
        synchronized (SongListenCountUpdateScheduler.class) {
            copyMap = songListensBuffer;
            songListensBuffer = Collections.synchronizedSet(new HashSet<>());
        }
        try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
            log.info("{} new listens are being saved into the database", copyMap.size());
            executor.submit(() -> {
                Map<Long, Long> songsToSave = new ConcurrentHashMap<>();
                Queue<UserSong> userListensToSave = new ConcurrentLinkedQueue<>();
                //this is not optimized, i'll have to look into this in the future, but this is the best i came up for now
                synchronized (copyMap) {
                    log.info("lock for songs starts");
                    copyMap.stream()
                            .parallel()
                            .forEach(e -> {
                                if (songsToSave.containsKey(e.getFirst().getFirst())) {
                                    songsToSave.put(
                                            e.getFirst().getFirst(),
                                            songsToSave.get(e.getFirst().getFirst()) + 1
                                    );
                                } else {
                                    songsToSave.put(e.getFirst().getFirst(), 1L);
                                }

                                if (e.getFirst().getSecond() != null) {
                                    userListensToSave.add(
                                            new UserSong(
                                                    e.getFirst().getFirst(),
                                                    e.getFirst().getSecond(),
                                                    e.getSecond()
                                            )
                                    );
                                }
                            });

                    songRepository.saveAll(
                            songRepository
                                    .findAllById(songsToSave.keySet())
                                    .stream()
                                    .parallel()
                                    .peek(song -> song.addListen(songsToSave.get(song.getId())))
                                    .toList()
                    );

                    userSongRepository.saveAll(userListensToSave);

                    log.info("lock for songs ends");
                }
                copyMap.clear();
            });
        }
    }
}
