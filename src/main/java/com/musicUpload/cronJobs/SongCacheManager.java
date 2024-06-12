package com.musicUpload.cronJobs;

import com.musicUpload.dataHandler.details.CustomUserDetails;
import com.musicUpload.dataHandler.models.implementations.Song;
import com.musicUpload.dataHandler.models.implementations.UserSong;
import com.musicUpload.dataHandler.repositories.SongRepository;
import com.musicUpload.dataHandler.repositories.UserSongRepository;
import com.musicUpload.util.Pair;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

@Service
public class SongCacheManager {
    private static final Logger logger = LogManager.getLogger(SongCacheManager.class);
    private final EntityCacheManager<Song> songEntityManager;
    private final SongRepository songRepository;
    private final UserSongRepository userSongRepository;
    private final int SCHEDULE = 2 * 1000 * 60;
    private final Map<Pair<Long, Long>, Long> songListensBuffer;
    @Getter
    private final Map<Pair<Long, Long>, Long> copyMap;

    @Autowired
    public SongCacheManager(EntityCacheManager<Song> songEntityManager,
                            SongRepository songRepository,
                            UserSongRepository userSongRepository) {
        this.songEntityManager = songEntityManager;
        this.songRepository = songRepository;
        this.userSongRepository = userSongRepository;
        this.songListensBuffer = new ConcurrentHashMap<>();
        this.copyMap = new ConcurrentHashMap<>();
    }

    public void addListenToSong(Long songId, CustomUserDetails userDetails) {
        new Thread(() -> {
            Long userId = userDetails == null ? null : userDetails.getId();
            songListensBuffer.merge(Pair.of(songId, userId), 1L, Long::sum);
            logger.info("buffer: {}", songListensBuffer);
            //I make sure here that the cached data is not stale
            songEntityManager.getEntity(songId)
                    .ifPresent(Song::addListen);
        }).start();
    }

    public void addSong(Song song) {
        songEntityManager.addEntity(song, song.getCacheIndex());
    }

    public void removeSong(Long id) {
        songEntityManager.removeEntity(id);
    }

    public Optional<Song> getSong(Long id) {
        return songEntityManager.getEntity(id);
    }

    @Scheduled(fixedRate = SCHEDULE)
    public void saveReport() {
        copyMap.putAll(songListensBuffer.entrySet().stream()
                        .filter(e -> songListensBuffer.remove(e.getKey(), e.getValue()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        //TODO: look into this, it is possible that this can lead to data loss, if a request arrives after the copy and before the clear
        // but this might not even be a big enough issue to worry about, bc listenCount will never be accurate
        songListensBuffer.clear();
        logger.info("{} new listens are being saved into the database", copyMap.size());
        new Thread(() -> {
            Map<Long, Long> songsToSave = new ConcurrentHashMap<>();
            Queue<UserSong> userListensToSave = new ConcurrentLinkedQueue<>();
            //this is not optimized, i'll have to look into this in the future, but this is the best i came up for now
            synchronized (copyMap) {
                logger.info("lock for songs starts");
                copyMap.entrySet().stream()
                        .parallel()
                        .forEach(e -> {
                            if(songsToSave.containsKey(e.getKey().getFirst())) {
                                songsToSave.put(e.getKey().getFirst(), songsToSave.get(e.getKey().getFirst()) + e.getValue());
                            }
                            else {
                                songsToSave.put(e.getKey().getFirst(), e.getValue());
                            }

                            LocalDate date = LocalDate.now();
                            Date firstDate = Date.from(date.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
                            Date lastDate = Date.from(date.withDayOfMonth(date.lengthOfMonth()).atStartOfDay(ZoneId.systemDefault()).toInstant());
                            var userListenOpt = userSongRepository.findBySongIdAndUserIdAndCreatedAtBetween(e.getKey().getFirst(),
                                                                                                         e.getKey().getSecond(),
                                                                                                         firstDate,
                                                                                                         lastDate);

                            userListenOpt.ifPresentOrElse(
                                    u -> {
                                        u.setListenCount(u.getListenCount() + e.getValue());
                                        userListensToSave.add(u);
                                    },
                                    () -> {
                                        if(e.getKey().getSecond() != null) {
                                            userListensToSave.add(new UserSong(e.getKey().getFirst(),
                                                                               e.getKey().getSecond(),
                                                                               e.getValue()));
                                        }
                                    }
                            );
                        });
                List<Song> songList = songsToSave.entrySet().stream().map(entry -> {
                    var s = songRepository.findById(entry.getKey());
                    s.ifPresent(song -> song.addListen(entry.getValue()));
                    return s;
                }).filter(Optional::isPresent).map(Optional::get).toList();
                songRepository.saveAll(songList);
                userSongRepository.saveAll(userListensToSave);
                logger.info("lock for songs ends");
            }
            copyMap.clear();
        }).start();
    }
}
