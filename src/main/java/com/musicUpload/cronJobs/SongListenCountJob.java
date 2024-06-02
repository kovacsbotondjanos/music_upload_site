package com.musicUpload.cronJobs;

import com.musicUpload.dataHandler.models.implementations.Song;
import com.musicUpload.dataHandler.repositories.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class SongListenCountJob {
    private ConcurrentMap<Long, Long> userSongMap;
    private final SongRepository songRepository;
    private final int SCHEDULE = 5 * 1000 * 60;

    @Autowired
    public SongListenCountJob(SongRepository songRepository) {
        this.songRepository = songRepository;
        userSongMap = new ConcurrentHashMap<>();
    }

    public void addListenToSong(Long songId) {
        userSongMap.merge(songId, 1L, Long::sum);
    }

    @Scheduled(fixedRate = SCHEDULE)
    public void saveReport(){
        var copyMap = userSongMap;
        copyMap.forEach((s, l) -> {
            Optional<Song> songOpt = songRepository.findById(s);
            songOpt.ifPresent(song -> {
                song.addListen(l);
                songRepository.save(song);
            });
        });
        userSongMap = new ConcurrentHashMap<>();
    }
}
