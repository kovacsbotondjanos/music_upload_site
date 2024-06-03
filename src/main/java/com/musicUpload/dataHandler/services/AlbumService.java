package com.musicUpload.dataHandler.services;

import com.musicUpload.cronJobs.EntityManager;
import com.musicUpload.dataHandler.DTOs.AlbumDTO;
import com.musicUpload.dataHandler.details.CustomUserDetails;
import com.musicUpload.dataHandler.models.implementations.Album;
import com.musicUpload.dataHandler.models.implementations.ProtectionType;
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
    private final EntityManager<Album> albumEntityManager;

    @Autowired
    public AlbumService(AlbumRepository albumRepository, UserRepository userRepository,
                        ProtectionTypeService protectionTypeService,
                        SongService songService,
                        ImageFactory imageFactory, EntityManager<Album> albumEntityManager) {
        this.albumRepository = albumRepository;
        this.userRepository = userRepository;
        this.protectionTypeService = protectionTypeService;
        this.songService = songService;
        this.imageFactory = imageFactory;
        this.albumEntityManager = albumEntityManager;
    }

    public void saveAlbum(CustomUserDetails userDetails,
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

        ProtectionType protection = protectionTypeService.getProtectionTypeByName(protectionType)
                .orElseThrow(NotFoundException::new);

        album.setProtectionType(protection);

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(NotFoundException::new);

        album.setUser(user);

        album.setName(name);

        if (image != null && !image.isEmpty()) {
            try {
                if (!Objects.requireNonNull(image.getContentType()).contains("image")) {
                    throw new UnprocessableException();
                }
                String hashedFileName = UUID.randomUUID() + ".jpg";
                image.transferTo(new File(imageFactory.getDirName() + FileSystems.getDefault().getSeparator() + hashedFileName));
                imageFactory.deleteFile(album.getImage());
                album.setImage(hashedFileName);
            } catch (IOException ioException) {
                throw new UnprocessableException();
            }
        } else {
            String img = imageFactory.getRandomImage();
            album.setImage(img);
        }

        Album a = albumRepository.save(album);
        userDetails.getAlbums().add(a);
    }

    public Album saveAlbum(Album album) {
        return albumRepository.save(album);
    }

    public List<AlbumDTO> getAlbums(CustomUserDetails userDetails) {
        if (userDetails == null) {
            throw new UnauthenticatedException();
        }
        return userDetails.getAlbums().stream().map(AlbumDTO::new).toList();
    }

    public AlbumDTO findById(Long id, CustomUserDetails userDetails) {
        Album album = findById(id)
                .orElseThrow(NotFoundException::new);
        if (!album.getProtectionType().getName().equals("PRIVATE") ||
                userDetails != null && album.getUser().getId().equals(userDetails.getId())) {
            return AlbumDTO.of(album);
        }
        throw new UnauthenticatedException();
    }

    public List<AlbumDTO> findByNameLike(CustomUserDetails userDetails, String name) {
        List<AlbumDTO> albums = albumRepository.findByNameLike(name).stream().map(AlbumDTO::new).toList();
        if (userDetails == null) {
            return albums.stream().filter(s -> s.getProtectionType().equals("PUBLIC")).limit(10).toList();
        }
        return albums.stream()
                .filter(s -> s.getProtectionType().equals("PUBLIC")
                        || s.getUserId().equals(userDetails.getId())).limit(10).toList();
    }

    public Album patchAlbum(CustomUserDetails userDetails,
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
            Optional<ProtectionType> protectionOpt = protectionTypeService.getProtectionTypeByName(protectionType);
            protectionOpt.ifPresent(album::setProtectionType);
        }

        if (name != null) {
            album.setName(name);
        }

        if (image != null && !image.isEmpty()) {
            try {
                if (!Objects.requireNonNull(image.getContentType()).contains("image")) {
                    throw new UnprocessableException();
                }
                String hashedFileName = UUID.randomUUID() + ".jpg";
                image.transferTo(new File(imageFactory.getDirName() + FileSystems.getDefault().getSeparator() + hashedFileName));
                imageFactory.deleteFile(album.getImage());
                album.setImage(hashedFileName);
            } catch (IOException ioException) {
                throw new UnprocessableException();
            }
        }

        if (songIds != null) {
            songIds.forEach(songId -> {
                songService.findById(songId).ifPresent(song -> {
                    if (!song.getProtectionType().getName().equals("PRIVATE")
                            || album.getUser().getSongs().stream()
                            .anyMatch(s -> s.getId().equals(song.getId()))) {
                        album.getSongs().add(song);
                    }
                });
            });
        }

        return albumRepository.save(album);
    }

    public Album deleteAlbum(CustomUserDetails userDetails,
                             Long id) {
        if (userDetails == null) {
            throw new UnauthenticatedException();
        }

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(NotFoundException::new);

        Album album = user.getAlbums().stream()
                .filter(a -> a.getId().equals(id)).findAny()
                .orElseThrow(UnauthenticatedException::new);

        user.getAlbums().remove(album);
        userRepository.save(user);

        ProtectionType protectionType = album.getProtectionType();
        protectionType.getAlbums().remove(album);
        protectionTypeService.save(protectionType);

        imageFactory.deleteFile(album.getImage());

        albumRepository.delete(album);
        userDetails.getAlbums().remove(album);

        return album;
    }

    private Optional<Album> findById(Long id) {
        return albumRepository.findById(id);
    }
}
