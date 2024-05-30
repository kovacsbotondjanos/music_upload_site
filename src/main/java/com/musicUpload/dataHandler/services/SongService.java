package com.musicUpload.dataHandler.services;

import com.musicUpload.dataHandler.DTOs.SongDTO;
import com.musicUpload.dataHandler.details.CustomUserDetails;
import com.musicUpload.dataHandler.models.ProtectionType;
import com.musicUpload.dataHandler.models.Song;
import com.musicUpload.dataHandler.models.User;
import com.musicUpload.dataHandler.repositories.AlbumRepository;
import com.musicUpload.dataHandler.repositories.SongRepository;
import com.musicUpload.dataHandler.repositories.UserRepository;
import com.musicUpload.exceptions.FileIsInWrongFormatException;
import com.musicUpload.exceptions.UnauthenticatedException;
import com.musicUpload.exceptions.UserNotFoundException;
import com.musicUpload.util.ImageFactory;
import com.musicUpload.util.MusicFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.*;

@Service
public class SongService {
    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final AlbumRepository albumRepository;
    private final ImageFactory imageFactory;
    private final MusicFactory musicFactory;
    private final ProtectionTypeService protectionTypeService;

    @Autowired
    public SongService(SongRepository songRepository, UserRepository userRepository, AlbumRepository albumRepository, ImageFactory imageFactory, MusicFactory songFactory, ProtectionTypeService protectionTypeService) {
        this.songRepository = songRepository;
        this.userRepository = userRepository;
        this.albumRepository = albumRepository;
        this.imageFactory = imageFactory;
        this.musicFactory = songFactory;
        this.protectionTypeService = protectionTypeService;
    }

    public Song saveSong(Song song){
        return songRepository.save(song);
    }

    public Song saveSong(CustomUserDetails userDetails,
                         String protectionType,
                         String name,
                         MultipartFile image,
                         MultipartFile songFile){
        if(userDetails == null){
            throw new UnauthenticatedException();
        }

        if(name == null || protectionType == null || songFile == null){
            throw new IllegalArgumentException();
        }

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(IllegalArgumentException::new);

        Song song = new Song();
        song.setUser(user);

        ProtectionType protection = protectionTypeService.getProtectionTypeByName(protectionType)
                .orElseThrow(IllegalAccessError::new);
        song.setProtectionType(protection);

        song.setName(name);

        if(image != null && !image.isEmpty()){
            try{
                if(!Objects.requireNonNull(image.getContentType()).contains("image")){
                    throw new FileIsInWrongFormatException();
                }
                String hashedFileName = UUID.randomUUID() + ".jpg";
                image.transferTo(new File(imageFactory.getDirName() + FileSystems.getDefault().getSeparator() + hashedFileName));
                imageFactory.deleteFile(song.getImage());
                song.setImage(hashedFileName);
            }
            catch (IOException ioException){
                throw new FileIsInWrongFormatException();
            }
        }
        else{
            String img = imageFactory.getRandomImage();
            song.setImage(img);
        }

        if(!songFile.isEmpty()) {
            try {
                if (!Objects.requireNonNull(songFile.getContentType()).contains("audio")) {
                    throw new IllegalArgumentException();
                }
                String hashedFileName = UUID.randomUUID() + ".mp3";
                songFile.transferTo(new File(musicFactory.getDirName() + FileSystems.getDefault().getSeparator() + hashedFileName));
                musicFactory.deleteFile(song.getNameHashed());
                song.setNameHashed(hashedFileName);
            } catch (IOException ioException) {
                //TODO: create a custom exception here
                throw new IllegalArgumentException();
            }
        }

        return saveSong(song);
    }

    public Optional<Song> findById(Long id){
        return songRepository.findById(id);
    }

    public SongDTO findById(CustomUserDetails userDetails,
                            Long id){
        Optional<Song> songOptional = findById(id);
        if(songOptional.isPresent() && !songOptional.get().getProtectionType().getName().equals("PRIVATE")){
            return SongDTO.of(songOptional.get());
        }

        if(userDetails != null){
            Optional<Song> songOptionalForUser = userDetails.getSongs().stream().filter(s -> s.getId().equals(id)).findAny();

            if(songOptionalForUser.isPresent()){
                return SongDTO.of(songOptionalForUser.get());
            }
        }
        throw new UnauthenticatedException();
    }

    public Song deleteSong(CustomUserDetails userDetails,
                           Long id){
        if(userDetails == null){
            throw new UnauthenticatedException();
        }

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(UserNotFoundException::new);

        Song song = user.getSongs().stream()
                .filter(s -> s.getId().equals(id))
                .findAny()
                .orElseThrow(UnauthenticatedException::new);

        user.getSongs().remove(song);
        userRepository.save(user);

        ProtectionType protectionType = song.getProtectionType();
        protectionType.getSongs().remove(song);
        protectionTypeService.save(protectionType);

        song.getAlbums().forEach(a -> {
            a.getSongs().remove(song);
            albumRepository.save(a);
        });

        songRepository.delete(song);
        imageFactory.deleteFile(song.getImage());
        musicFactory.deleteFile(song.getNameHashed());

        userDetails.getSongs().remove(song);

        return song;
    }

    public List<SongDTO> getRandomSongs(){
        return songRepository.findRandomSongs().stream()
                .map(SongDTO::new)
                .toList();
    }

    public Optional<Song> findByNameHashed(String name){
        return songRepository.findByNameHashed(name);
    }

    public void updateSong(CustomUserDetails userDetails,
                           Long id,
                           String protectionType,
                           String name,
                           MultipartFile image){
        if(userDetails == null){
            throw new UnauthenticatedException();
        }

        Song song = userDetails.getSongs().stream().filter(s -> s.getId().equals(id)).findAny()
                .orElseThrow(UnauthenticatedException::new);

        if(protectionType != null){
            Optional<ProtectionType> protectionOpt = protectionTypeService.getProtectionTypeByName(protectionType);
            protectionOpt.ifPresent(song::setProtectionType);
        }

        if(name != null){
            song.setName(name);
        }

        if(image != null && image.isEmpty()){
            try{
                if(!Objects.requireNonNull(image.getContentType()).contains("image")){
                    throw new IllegalArgumentException();
                }
                String hashedFileName = UUID.randomUUID() + ".jpg";
                image.transferTo(new File(imageFactory.getDirName() + FileSystems.getDefault().getSeparator() + hashedFileName));
                imageFactory.deleteFile(song.getImage());
                song.setImage(hashedFileName);
            }
            catch (IOException ioException){
                //TODO: create a custom exception here
                throw new IllegalArgumentException();
            }
        }

        songRepository.save(song);
    }

    public List<SongDTO> getSongs(CustomUserDetails userDetails){
        if(userDetails == null){
            throw new UnauthenticatedException();
        }

        return userDetails.getSongs().stream().map(SongDTO::new).toList();
    }
}
