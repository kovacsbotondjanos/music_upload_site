package com.musicUpload.dataHandler.services;

import com.musicUpload.dataHandler.models.Album;
import com.musicUpload.dataHandler.repositories.AlbumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AlbumService {
    @Autowired
    private final AlbumRepository albumRepository;

    public AlbumService(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

    public Album saveAlbum(Album album){
        return albumRepository.save(album);
    }

    public void deleteAlbum(Album album){
        albumRepository.delete(album);
    }

    public Optional<Album> findById(Long id){
        return albumRepository.findById(id);
    }
}
