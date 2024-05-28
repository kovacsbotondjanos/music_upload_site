package com.musicUpload.dataHandler.services;

import com.musicUpload.dataHandler.DTOs.AlbumDTO;
import com.musicUpload.dataHandler.details.CustomUserDetails;
import com.musicUpload.dataHandler.models.Album;
import com.musicUpload.dataHandler.models.ProtectionType;
import com.musicUpload.dataHandler.models.User;
import com.musicUpload.dataHandler.repositories.AlbumRepository;
import com.musicUpload.dataHandler.repositories.UserRepository;
import com.musicUpload.exceptions.FileIsInWrongFormatException;
import com.musicUpload.exceptions.ImageCannotBeSavedException;
import com.musicUpload.exceptions.UnauthenticatedException;
import com.musicUpload.exceptions.UserNotFoundException;
import com.musicUpload.util.ImageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final UserRepository userRepository;
    private final ProtectionTypeService protectionTypeService;
    private final SongService songService;
    private final ImageFactory imageFactory;

    @Autowired
    public AlbumService(AlbumRepository albumRepository, UserRepository userRepository,
                        ProtectionTypeService protectionTypeService,
                        SongService songService,
                        ImageFactory imageFactory) {
        this.albumRepository = albumRepository;
        this.userRepository = userRepository;
        this.protectionTypeService = protectionTypeService;
        this.songService = songService;
        this.imageFactory = imageFactory;
    }

    public void saveAlbum(CustomUserDetails userDetails,
                           String protectionType,
                           String name,
                           MultipartFile image){
        if(userDetails == null){
            throw new UnauthenticatedException();
        }

        if (protectionType == null || name == null) {
            throw new IllegalArgumentException();
        }

        Album album = new Album();

        ProtectionType protection = protectionTypeService.getProtectionTypeByName(protectionType)
                .orElseThrow(IllegalArgumentException::new);

        album.setProtectionType(protection);

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(UserNotFoundException::new);

        album.setUser(user);

        album.setName(name);

        if(image != null && !image.isEmpty()){
            try{
                if(!Objects.requireNonNull(image.getContentType()).contains("image")){
                    throw new FileIsInWrongFormatException();
                }
                String hashedFileName = UUID.randomUUID() + ".jpg";
                image.transferTo(new File(imageFactory.getDirName() + FileSystems.getDefault().getSeparator() + hashedFileName));
                imageFactory.deleteFile(album.getImage());
                album.setImage(hashedFileName);
            }
            catch (IOException ioException){
                throw new ImageCannotBeSavedException();
            }
        }

        Album a = albumRepository.save(album);
        userDetails.getAlbums().add(a);
    }

    public Album saveAlbum(Album album){
        return albumRepository.save(album);
    }

    public Album deleteAlbum(CustomUserDetails userDetails,
                            Long id){
        if(userDetails == null){
            throw new UnauthenticatedException();
        }

        Album album = userDetails.getAlbums().stream().filter(a -> a.getId().equals(id)).findAny()
                .orElseThrow(UnauthenticatedException::new);

        albumRepository.delete(album);
        userDetails.getAlbums().remove(album);

        return album;
    }

    public AlbumDTO findById(Long id, CustomUserDetails userDetails){
        Optional<Album> albumOptional = findById(id);
        if(albumOptional.isPresent() && !albumOptional.get().getProtectionType().getName().equals("PRIVATE")){
            return AlbumDTO.of(albumOptional.get());
        }

        if (userDetails != null) {
            albumOptional = userDetails.getAlbums().stream().filter(a -> a.getId().equals(id)).findAny();
            if(albumOptional.isPresent()){
                return AlbumDTO.of(albumOptional.get());
            }
        }
        throw new UnauthenticatedException();
    }

    public Album patchAlbum(CustomUserDetails userDetails,
                           Long id,
                           String protectionType,
                           List<Long> songIds,
                           String name,
                           MultipartFile image){
        if(userDetails == null){
            throw new UnauthenticatedException();
        }

        Album album = userDetails.getAlbums().stream().filter(a -> a.getId().equals(id)).findAny()
                .orElseThrow(UnauthenticatedException::new);
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
                    throw new FileIsInWrongFormatException();
                }
                String hashedFileName = UUID.randomUUID() + ".jpg";
                image.transferTo(new File(imageFactory.getDirName() + FileSystems.getDefault().getSeparator() + hashedFileName));
                imageFactory.deleteFile(album.getImage());
                album.setImage(hashedFileName);
            }
            catch (IOException ioException){
                throw new ImageCannotBeSavedException();
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

        return albumRepository.save(album);
    }

    public List<AlbumDTO> getAlbums(CustomUserDetails userDetails){
        if(userDetails == null){
            throw new UnauthenticatedException();
        }
        return userDetails.getAlbums().stream().map(AlbumDTO::new).toList();
    }

    private Optional<Album> findById(Long id){
        return albumRepository.findById(id);
    }
}
