package com.musicUpload.dataHandler.services;

import com.musicUpload.dataHandler.models.Album;
import com.musicUpload.dataHandler.models.ProtectionType;
import com.musicUpload.dataHandler.repositories.AlbumRepository;
import com.musicUpload.util.ImageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final ProtectionTypeService protectionTypeService;
    private final SongService songService;
    private final ImageFactory imageFactory;

    @Autowired
    public AlbumService(AlbumRepository albumRepository, ProtectionTypeService protectionTypeService, SongService songService, ImageFactory imageFactory) {
        this.albumRepository = albumRepository;
        this.protectionTypeService = protectionTypeService;
        this.songService = songService;
        this.imageFactory = imageFactory;
    }

    public void saveAlbum(String protectionType,
                          String name,
                          MultipartFile image){
        if(protectionType == null || name == null){
            throw new IllegalArgumentException();
        }
        Album album = new Album();

        Optional<ProtectionType> protectionOpt = protectionTypeService.getProtectionTypeByName(protectionType);
        album.setProtectionType(protectionOpt
                .orElseThrow(IllegalArgumentException::new));

        album.setName(name);
        //TODO: create a function for this
        if(image != null && image.isEmpty()){
            try{
                if(!Objects.requireNonNull(image.getContentType()).contains("image")){
                    throw new IllegalArgumentException();
                }
                String hashedFileName = UUID.randomUUID() + ".jpg";
                image.transferTo(new File(imageFactory.getDirName() + "//" + hashedFileName));
                imageFactory.deleteFile(album.getImage());
                album.setImage(hashedFileName);
            }
            catch (IOException ioException){
                //TODO: create a custom exception here
                throw new IllegalArgumentException();
            }
        }
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

    public void patchAlbum(Album album,
                           String protectionType,
                           List<Long> songIds,
                           String name,
                           MultipartFile image){
        if(protectionType != null){
            Optional<ProtectionType> protectionOpt = protectionTypeService.getProtectionTypeByName(protectionType);
            protectionOpt.ifPresent(album::setProtectionType);
        }

        if(name != null){
            album.setName(name);
        }

        if(image != null && image.isEmpty()){
            try{
                if(!Objects.requireNonNull(image.getContentType()).contains("image")){
                    throw new IllegalArgumentException();
                }
                String hashedFileName = UUID.randomUUID() + ".jpg";
                image.transferTo(new File(imageFactory.getDirName() + "//" + hashedFileName));
                imageFactory.deleteFile(album.getImage());
                album.setImage(hashedFileName);
            }
            catch (IOException ioException){
                //TODO: create a custom exception here
                throw new IllegalArgumentException();
            }
        }

        if(songIds != null){
            songIds.forEach(songId -> {
                songService.findById(songId).ifPresent(song -> {
                    if(!song.getProtectionType().getName().equals("PRIVATE")
                            || album.getUser().getSongs().equals(song)){
                        album.getSongs().add(song);
                    }
                });
            });
        }

        albumRepository.save(album);
    }
}
