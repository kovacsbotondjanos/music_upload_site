package com.musicUpload.cronJobs;

import com.musicUpload.dataHandler.models.implementations.Song;
import com.musicUpload.dataHandler.repositories.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

@Service
public class SongCacheManager {
    private final EntityManager<Song> songEntityManager;
    private final SongRepository songRepository;
    private final int SCHEDULE = 5 * 1000 * 60;
    private ConcurrentMap<Long, Long> songListensBuffer;

    @Autowired
    public SongCacheManager(EntityManager<Song> songEntityManager, SongRepository songRepository) {
        this.songEntityManager = songEntityManager;
        this.songRepository = songRepository;
        songListensBuffer = new ConcurrentHashMap<>();
    }

    public void addListenToSong(Long songId) {
        new Thread(() -> {
            songListensBuffer.merge(songId, 1L, Long::sum);
            songEntityManager.getEntity(songId)
                    .ifPresent(Song::addListen);
        }).start();
    }

    public void addSong(Song song) {
        songEntityManager.addEntity(song);
    }

    public void removeSong(Long id) {
        songEntityManager.removeEntity(id);
    }

    public Optional<Song> getSong(Long id) {
        return songEntityManager.getEntity(id);
    }

    @Scheduled(fixedRate = SCHEDULE)
    public void saveReport() {
        var copyMap = songListensBuffer;
        songListensBuffer = new ConcurrentHashMap<>();
        new Thread(() -> {
            ConcurrentLinkedQueue<Song> songsToSave = new ConcurrentLinkedQueue<>();
            copyMap.entrySet().stream()
                    .parallel()
                    .forEach(e -> {
                        Optional<Song> songOpt = songRepository.findById(e.getKey());
                        songOpt.ifPresent(song -> {
                            song.addListen(e.getValue());
                            songsToSave.add(song);
                        });
                    });
            songRepository.saveAll(new ArrayList<>(songsToSave));
        });
    }
}
