package com.musicUpload.cronJobs;

import com.musicUpload.dataHandler.details.CustomUserDetails;
import com.musicUpload.dataHandler.models.implementations.Song;
import com.musicUpload.dataHandler.models.implementations.UserSong;
import com.musicUpload.dataHandler.repositories.SongRepository;
import com.musicUpload.dataHandler.repositories.UserSongRepository;
import com.musicUpload.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Service
public class SongCacheManager {
    private final EntityCacheManager<Song> songEntityManager;
    private final SongRepository songRepository;
    private final UserSongRepository userSongRepository;
    private final int SCHEDULE = 2 * 1000 * 60;
    private ConcurrentMap<Pair<Long, Long>, Long> songListensBuffer;

    @Autowired
    public SongCacheManager(EntityCacheManager<Song> songEntityManager, SongRepository songRepository, UserSongRepository userSongRepository) {
        this.songEntityManager = songEntityManager;
        this.songRepository = songRepository;
        this.userSongRepository = userSongRepository;
        songListensBuffer = new ConcurrentHashMap<>();
    }

    public void addListenToSong(Long songId, CustomUserDetails userDetails) {
        new Thread(() -> {
            Long userId = userDetails == null ? null : userDetails.getId();
            songListensBuffer.merge(new Pair<>(songId, userId), 1L, Long::sum);
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
        var copyMap = songListensBuffer.entrySet().stream()
                        .filter(e -> songListensBuffer.remove(e.getKey(), e.getValue()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        //TODO: look into this, it is possible that this can lead to data loss, if a request arrives after the copy and before the clear
        songListensBuffer.clear();

        new Thread(() -> {
            ConcurrentLinkedQueue<Song> songsToSave = new ConcurrentLinkedQueue<>();
            ConcurrentLinkedQueue<UserSong> userListensToSave = new ConcurrentLinkedQueue<>();
            copyMap.entrySet().stream()
                    .parallel()
                    .forEach(e -> {
                        Optional<Song> songOpt = songRepository.findById(e.getKey().getFirst());
                        songOpt.ifPresent(song -> {
                            song.addListen(e.getValue());
                            songsToSave.add(song);
                        });
                        Optional<UserSong> userListenOpt = userSongRepository.findBySongIdAndUserId(e.getKey().getFirst(), e.getKey().getSecond());
                        Long listens = userListenOpt.map(userSong -> userSong.getListenCount() + 1).orElse(1L);
                        userListenOpt.ifPresentOrElse(
                                userListensToSave::add,
                                () -> {
                                    if(e.getKey().getSecond() != null) {
                                        userListensToSave.add(
                                                new UserSong(
                                                        e.getKey().getFirst(),
                                                        e.getKey().getSecond(),
                                                        listens));
                                    }
                                }
                        );
                    });
            System.out.println(songsToSave);
            System.out.println(userListensToSave);
            songRepository.saveAll(songsToSave);
            //TODO: this can be saved less frequently
            userSongRepository.saveAll(userListensToSave);
        }).start();
    }
}
