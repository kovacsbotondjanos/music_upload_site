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
    private final Map<Pair<Long, Long>, Long> songListensBuffer;

    @Autowired
    public SongListenCountUpdateScheduler(SongRepository songRepository,
                                          UserSongRepository userSongRepository) {
        this.songRepository = songRepository;
        this.userSongRepository = userSongRepository;
        this.songListensBuffer = new ConcurrentHashMap<>();
    }

    public void addListenToSong(Long songId, UserDetailsImpl userDetails) {
        new Thread(() -> {
            Long userId = userDetails == null ? null : userDetails.getId();
            songListensBuffer.merge(Pair.of(songId, userId), 1L, Long::sum);
            log.info("buffer: {}", songListensBuffer);
        }).start();
    }

    @Scheduled(fixedRate = SCHEDULE)
    @SchedulerLock(name = "SongListenCountUpdateScheduler_saveReport", lockAtMostFor = "2m")
    public void saveReport() {
        Map<Pair<Long, Long>, Long> copyMap;
        synchronized (songListensBuffer) {
            copyMap = new ConcurrentHashMap<>(songListensBuffer);
            songListensBuffer.clear();
        }
        try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
            log.info("{} new listens are being saved into the database", copyMap.size());
            executor.submit(() -> {
                Map<Long, Long> songsToSave = new ConcurrentHashMap<>();
                Queue<UserSong> userListensToSave = new ConcurrentLinkedQueue<>();
                //this is not optimized, i'll have to look into this in the future, but this is the best i came up for now
                synchronized (copyMap) {
                    log.info("lock for songs starts");
                    copyMap.entrySet()
                        .stream()
                        .parallel()
                        .forEach(e -> {
                            if (songsToSave.containsKey(e.getKey().getFirst())) {
                                songsToSave.put(
                                    e.getKey().getFirst(),
                                    songsToSave.get(e.getKey().getFirst()) + e.getValue()
                                );
                            } else {
                                songsToSave.put(e.getKey().getFirst(), e.getValue());
                            }

                            //monthly reports of songs
                            Date firstDate = getFirstDayOfTheMonth();
                            Date lastDate = getLastDayOfTheMonth();
                            //TODO: check if we could fetch by a list of ids here
                            Optional<UserSong> userListenOpt = userSongRepository
                                .findBySongIdAndUserIdAndCreatedAtBetween(
                                    e.getKey().getFirst(),
                                    e.getKey().getSecond(),
                                    firstDate,
                                    lastDate
                                );

                            userListenOpt.ifPresentOrElse(
                                u -> {
                                    u.setListenCount(u.getListenCount() + e.getValue());
                                    userListensToSave.add(u);
                                },
                                () -> {
                                    if (e.getKey().getSecond() != null) {
                                        userListensToSave.add(
                                            new UserSong(
                                                e.getKey().getFirst(),
                                                e.getKey().getSecond(),
                                                e.getValue()
                                            )
                                        );
                                    }
                                }
                            );
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

    private Date getFirstDayOfTheMonth() {
        LocalDate date = LocalDate.now();
        return Date.from(
            date
                .withDayOfMonth(1)
                .atStartOfDay(
                        ZoneId.systemDefault()
                ).toInstant()
        );
    }

    private Date getLastDayOfTheMonth() {
        LocalDate date = LocalDate.now();
        return Date.from(
            date
                .withDayOfMonth(date.lengthOfMonth())
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
        );
    }
}
