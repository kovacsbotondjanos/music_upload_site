package com.musicUpload.dataHandler.services;

import com.musicUpload.cronJobs.EntityCacheManager;
import com.musicUpload.dataHandler.DTOs.AlbumDTO;
import com.musicUpload.dataHandler.details.UserDetailsImpl;
import com.musicUpload.dataHandler.enums.ProtectionType;
import com.musicUpload.dataHandler.models.implementations.Album;
import com.musicUpload.dataHandler.models.implementations.User;
import com.musicUpload.dataHandler.repositories.AlbumRepository;
import com.musicUpload.dataHandler.repositories.UserRepository;
import com.musicUpload.exceptions.NotFoundException;
import com.musicUpload.exceptions.UnauthenticatedException;
import com.musicUpload.exceptions.UnprocessableException;
import com.musicUpload.exceptions.WrongFormatException;
import com.musicUpload.util.ImageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final UserRepository userRepository;
    private final SongService songService;
    private final ImageFactory imageFactory;
//    private final EntityCacheManager<Album> albumCacheManager;
    private final MinioService minioService;
    private final SessionService sessionService;

    @Autowired
    public AlbumService(AlbumRepository albumRepository, UserRepository userRepository,
                        SongService songService,
                        ImageFactory imageFactory,
                        EntityCacheManager<Album> albumCacheManager,
                        MinioService minioService, SessionService sessionService) {
        this.albumRepository = albumRepository;
        this.userRepository = userRepository;
        this.songService = songService;
        this.imageFactory = imageFactory;
//        this.albumCacheManager = albumCacheManager;
        this.minioService = minioService;
        this.sessionService = sessionService;
    }

    public Album saveAlbum(Album album) {
        Album a = albumRepository.save(album);
//        albumCacheManager.addEntity(a);
        return a;
    }

    public Album saveAlbum(UserDetailsImpl userDetails,
                           String protectionType,
                           String name,
                           MultipartFile image) {
        if (userDetails == null) {
            throw new UnauthenticatedException();
        }

        if (protectionType == null || name == null) {
            throw new WrongFormatException();
        }

        Album album = new Album();

        album.setProtectionType(ProtectionType.getByName(protectionType));

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(NotFoundException::new);

        album.setUser(user);

        album.setName(name);

        if (image != null && !image.isEmpty()) {
            if (!Objects.requireNonNull(image.getContentType()).contains("image")) {
                throw new UnprocessableException();
            }
            album.setImage(minioService.uploadImage(image));
        } else {
            String img = imageFactory.getRandomImage();
            album.setImage(img);
        }

        Album a = saveAlbum(album);
//        userDetails.addAlbum(a);
        sessionService.addAlbum(a);
        return a;
    }

    public List<AlbumDTO> getAlbums(UserDetailsImpl userDetails) {
        if (userDetails == null) {
            throw new UnauthenticatedException();
        }
        return userDetails.getAlbums().stream().map(AlbumDTO::new).toList();
    }

    private Optional<Album> findById(Long id) {
//        Optional<Album> a = albumCacheManager.getEntity(id);
//        if (a.isEmpty()) {
            //we only use this once, and if the opt is not empty we put it in the entityManager
            return albumRepository.findById(id);
//        }
//        return a;
    }

    public AlbumDTO findById(Long id, UserDetailsImpl userDetails) {
        Album album = findById(id)
                .orElseThrow(NotFoundException::new);
//        albumCacheManager.addEntity(album);
        if (!album.getProtectionType().equals(ProtectionType.PRIVATE) ||
                userDetails != null && album.getUser().getId().equals(userDetails.getId())) {
            return AlbumDTO.of(album);
        }
        throw new UnauthenticatedException();
    }

    //TODO: pagination
    public List<AlbumDTO> findByNameLike(UserDetailsImpl userDetails, String name) {
        List<Album> albums = albumRepository.findByNameLike(name);

        if (userDetails == null) {
            return albums.stream()
//                    .peek(albumCacheManager::addEntity)
                    .filter(a -> a.getProtectionType().equals(ProtectionType.PUBLIC))
                    .map(AlbumDTO::of)
                    .limit(10)
                    .toList();
        }

        return albums.stream()
//                .peek(albumCacheManager::addEntity)
                .filter(a -> a.getProtectionType().equals(ProtectionType.PUBLIC)
                        || a.getUser().getId().equals(userDetails.getId()))
                .map(AlbumDTO::of)
                .limit(10)
                .toList();
    }

    public Album patchAlbum(UserDetailsImpl userDetails,
                            Long id,
                            String protectionType,
                            List<Long> songIds,
                            String name,
                            MultipartFile image) {
        if (userDetails == null) {
            throw new UnauthenticatedException();
        }

        Album album = userDetails.getAlbums().stream().filter(a -> a.getId().equals(id)).findAny()
                .orElseThrow(UnauthenticatedException::new);
        if (protectionType != null) {
            album.setProtectionType(ProtectionType.getByName(protectionType));
        }

        if (name != null) {
            album.setName(name);
        }

        if (image != null && !image.isEmpty()) {
            if (!Objects.requireNonNull(image.getContentType()).contains("image")) {
                throw new UnprocessableException();
            }
            minioService.deleteImage(album.getImage());
            album.setImage(minioService.uploadImage(image));
        }

        if (songIds != null) {
            songIds.forEach(songId -> {
                songService.findById(songId).ifPresent(song -> {
                    if (!song.getProtectionType().equals(ProtectionType.PRIVATE)
                            || album.getUser().getSongs().stream()
                            .anyMatch(s -> s.getId().equals(song.getId()))) {
                        album.getSongs().add(song);
                    }
                });
            });
        }

        return albumRepository.save(album);
//        userDetails.addAlbum(a);
//        albumCacheManager.addEntity(a);
//        return a;
    }

    public Album addSongs(UserDetailsImpl userDetails,
                          Long id,
                          List<Long> songIds) {
        if (userDetails == null) {
            throw new UnauthenticatedException();
        }

        Album album = userDetails.getAlbums().stream().filter(a -> a.getId().equals(id)).findAny()
                .orElseThrow(UnauthenticatedException::new);
//        albumCacheManager.addEntity(album);

        if (songIds != null) {
            songIds.forEach(songId -> {
                songService.findById(songId).ifPresent(song -> {
                    if (!song.getProtectionType().equals(ProtectionType.PRIVATE)
                            || album.getUser().getSongs().stream()
                            .anyMatch(s -> s.getId().equals(song.getId()))) {
                        album.getSongs().add(song);
                    }
                });
            });
        }
        return albumRepository.save(album);
    }

    public Album deleteAlbum(UserDetailsImpl userDetails,
                             Long id) {
        if (userDetails == null) {
            throw new UnauthenticatedException();
        }

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(NotFoundException::new);

        Album album = userDetails.getAlbums()
                .stream()
                .filter(a -> a.getId().equals(id)).findAny()
                .orElseThrow(UnauthenticatedException::new);

//        Album album = user.getAlbums().stream()
//                .filter(a -> a.getId().equals(id)).findAny()
//                .orElseThrow(UnauthenticatedException::new);

        user.getAlbums().remove(album);
        userRepository.save(user);

        minioService.deleteImage(album.getImage());
        sessionService.deleteAlbum(album);
        albumRepository.delete(album);

//        userDetails.getAlbums().remove(album);
//        albumCacheManager.removeEntity(album.getId());

        return album;
    }
}
