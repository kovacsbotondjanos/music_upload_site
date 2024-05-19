package com.musicUpload.databaseHandler.models.songs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SongService {
    @Autowired
    private final SongRepository songRepository;

    public SongService(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    public Song saveSong(Song song){
        return songRepository.save(song);
    }

    public void deleteSong(Song song){
        songRepository.delete(song);
    }

    public Optional<Song> findById(Long id){
        return songRepository.findById(id);
    }

    public List<Song> getRandomSongs(){
        return songRepository.findRandomSongs();
    }
}
