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
public class SongListenCountJob {
    private ConcurrentMap<Long, Long> songListensLast5Mins;
    private final SongRepository songRepository;
    private final int SCHEDULE = 5 * 1000 * 60;

    @Autowired
    public SongListenCountJob(SongRepository songRepository) {
        this.songRepository = songRepository;
        songListensLast5Mins = new ConcurrentHashMap<>();
    }

    public void addListenToSong(Long songId) {
        songListensLast5Mins.merge(songId, 1L, Long::sum);
    }

    @Scheduled(fixedRate = SCHEDULE)
    public void saveReport(){
        var copyMap = songListensLast5Mins;
        songListensLast5Mins = new ConcurrentHashMap<>();
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
    }
}
