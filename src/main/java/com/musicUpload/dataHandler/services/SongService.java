package com.musicUpload.dataHandler.services;

import com.musicUpload.dataHandler.DTOs.SongDTO;
import com.musicUpload.dataHandler.details.CustomUserDetails;
import com.musicUpload.dataHandler.models.ProtectionType;
import com.musicUpload.dataHandler.models.Song;
import com.musicUpload.dataHandler.models.User;
import com.musicUpload.dataHandler.repositories.SongRepository;
import com.musicUpload.dataHandler.repositories.UserRepository;
import com.musicUpload.exceptions.UnauthenticatedException;
import com.musicUpload.util.ImageFactory;
import com.musicUpload.util.MusicFactory;
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
public class SongService {
    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final ImageFactory imageFactory;
    private final MusicFactory musicFactory;
    private final ProtectionTypeService protectionTypeService;

    @Autowired
    public SongService(SongRepository songRepository, UserRepository userRepository, ImageFactory imageFactory, MusicFactory songFactory, ProtectionTypeService protectionTypeService) {
        this.songRepository = songRepository;
        this.userRepository = userRepository;
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
                         MultipartFile songFile,
                         Long userId){
        if(userDetails == null){
            throw new UnauthenticatedException();
        }

        User user = userRepository.findById(userId)
                .orElseThrow(IllegalArgumentException::new);

        Song song = new Song();
        song.setUser(user);
        ProtectionType protection = protectionTypeService.getProtectionTypeByName(protectionType)
                .orElseThrow(IllegalAccessError::new);
        song.setProtectionType(protection);

        if(name == null){
            throw new IllegalArgumentException();
        }
        song.setName(name);

        if(image != null && image.isEmpty()){
            try{
                if(!Objects.requireNonNull(image.getContentType()).contains("image")){
                    throw new IllegalArgumentException();
                }
                String hashedFileName = UUID.randomUUID() + ".jpg";
                image.transferTo(new File(imageFactory.getDirName() + "//" + hashedFileName));
                imageFactory.deleteFile(song.getImage());
                song.setImage(hashedFileName);
            }
            catch (IOException ioException){
                //TODO: create a custom exception here
                throw new IllegalArgumentException();
            }
        }

        if(songFile != null && songFile.isEmpty()) {
            try {
                if (!Objects.requireNonNull(songFile.getContentType()).contains("image")) {
                    throw new IllegalArgumentException();
                }
                String hashedFileName = UUID.randomUUID() + ".mp3";
                songFile.transferTo(new File(musicFactory.getDirName() + "//" + hashedFileName));
                musicFactory.deleteFile(song.getNameHashed());
                song.setNameHashed(hashedFileName);
            } catch (IOException ioException) {
                //TODO: create a custom exception here
                throw new IllegalArgumentException();
            }
        }
        return saveSong(song);
    }

    public Song deleteSong(CustomUserDetails userDetails,
                           Long id){
        if(userDetails == null){
            throw new UnauthenticatedException();
        }

        Song song = userDetails.getSongs().stream().filter(s -> s.getId().equals(id)).findAny()
                .orElseThrow(UnauthenticatedException::new);
        imageFactory.deleteFile(song.getImage());
        songRepository.delete(song);
        return song;
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
                image.transferTo(new File(imageFactory.getDirName() + "//" + hashedFileName));
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
