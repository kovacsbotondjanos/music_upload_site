package com.musicUpload.dataHandler.services;

import com.musicUpload.dataHandler.DTOs.AlbumDTO;
import com.musicUpload.dataHandler.details.UserDetailsImpl;
import com.musicUpload.dataHandler.enums.ProtectionType;
import com.musicUpload.dataHandler.models.implementations.Album;
import com.musicUpload.dataHandler.models.implementations.User;
import com.musicUpload.dataHandler.repositories.AlbumRepository;
import com.musicUpload.dataHandler.repositories.SongRepository;
import com.musicUpload.dataHandler.repositories.UserRepository;
import com.musicUpload.exceptions.NotFoundException;
import com.musicUpload.exceptions.UnauthenticatedException;
import com.musicUpload.exceptions.UnprocessableException;
import com.musicUpload.exceptions.WrongFormatException;
import com.musicUpload.util.ImageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final UserRepository userRepository;
    private final SongRepository songRepository;
    private final ImageFactory imageFactory;
    private final MinioService minioService;

    @Autowired
    public AlbumService(AlbumRepository albumRepository, UserRepository userRepository,
                        SongRepository songRepository,
                        ImageFactory imageFactory,
                        MinioService minioService) {
        this.albumRepository = albumRepository;
        this.userRepository = userRepository;
        this.songRepository = songRepository;
        this.imageFactory = imageFactory;
        this.minioService = minioService;
    }

    public Album saveAlbum(Album album) {
        return albumRepository.save(album);
    }

    public Album saveAlbum(String protectionType,
                           String name,
                           MultipartFile image) {
        UserDetailsImpl userDetails = UserService.getCurrentUserDetails();
        if (userDetails == null) {
            throw new UnauthenticatedException();
        }

        if (protectionType == null || name == null) {
            throw new WrongFormatException();
        }

        Album album = new Album();

        album.setProtectionType(ProtectionType.valueOf(protectionType));

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

        return saveAlbum(album);
    }

    public List<AlbumDTO> getAlbums() {
        UserDetailsImpl userDetails = UserService.getCurrentUserDetails();

        if (userDetails == null) {
            throw new UnauthenticatedException();
        }

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(UnauthenticatedException::new);

        return albumRepository.findByUser(user).stream().map(AlbumDTO::new).toList();
    }

    public AlbumDTO findById(Long id) {
        UserDetailsImpl userDetails = UserService.getCurrentUserDetails();
        Album album = albumRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        if (!album.getProtectionType().equals(ProtectionType.PRIVATE) ||
                userDetails != null && album.getUser().getId().equals(userDetails.getId())) {
            return AlbumDTO.of(album);
        }
        throw new UnauthenticatedException();
    }

    public List<AlbumDTO> findByIdsIn(List<Long> ids) {
        User user = userRepository.findById(UserService.getCurrentUserDetails().getId())
                .orElseThrow(UnauthenticatedException::new);
        return albumRepository.findByIdInAndUserOrIdInAndProtectionType(
                ids,
                user.getId(),
                ProtectionType.PUBLIC
            ).stream()
            .map(AlbumDTO::of)
            .toList();
    }

    public List<AlbumDTO> findByNameLike(String name, int pageNumber, int pageSize) {
        Long userId = Optional.ofNullable(
                UserService.getCurrentUserDetails()
            )
            .map(UserDetailsImpl::getId)
            .orElse(null);

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        return albumRepository.findByNameLike(
                name,
                userId,
                ProtectionType.PUBLIC,
                pageable
            )
            .stream()
            .map(AlbumDTO::of)
            .toList();
    }

    public Album patchAlbum(Long id,
                            String protectionType,
                            List<Long> songIds,
                            String name,
                            MultipartFile image) {
        UserDetailsImpl userDetails = UserService.getCurrentUserDetails();
        if (userDetails == null) {
            throw new UnauthenticatedException();
        }

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(UnauthenticatedException::new);

        Album album = albumRepository.findByUserAndId(user, id)
                .orElseThrow(UnauthenticatedException::new);

        if (protectionType != null) {
            album.setProtectionType(ProtectionType.valueOf(protectionType));
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
                songRepository.findById(songId).ifPresent(song -> {
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

    public Album addSongs(Long id,
                          List<Long> songIds) {
        UserDetailsImpl userDetails = UserService.getCurrentUserDetails();
        if (userDetails == null) {
            throw new UnauthenticatedException();
        }

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(UnauthenticatedException::new);

        Album album = albumRepository.findByUserAndId(user, id)
                .orElseThrow(UnauthenticatedException::new);

        if (songIds != null) {
            songIds.forEach(songId ->
                songRepository.findByIdAndProtectionTypeOrUser(
                        id,
                        user.getId(),
                        ProtectionType.PUBLIC
                ).ifPresent(song -> album.getSongs().add(song))
            );
        }
        return albumRepository.save(album);
    }

    public Album deleteAlbum(Long id) {
        UserDetailsImpl userDetails = UserService.getCurrentUserDetails();
        if (userDetails == null) {
            throw new UnauthenticatedException();
        }

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(NotFoundException::new);

        Album album = albumRepository.findByUserAndId(user, id)
                .orElseThrow(UnauthenticatedException::new);

        user.getAlbums().remove(album);
        userRepository.save(user);

        minioService.deleteImage(album.getImage());
        albumRepository.delete(album);

        return album;
    }
}
