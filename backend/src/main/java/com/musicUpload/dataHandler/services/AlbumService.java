package com.musicUpload.dataHandler.services;

import com.musicUpload.dataHandler.DTOs.AlbumCardDTO;
import com.musicUpload.dataHandler.DTOs.AlbumDTO;
import com.musicUpload.dataHandler.details.UserDetailsImpl;
import com.musicUpload.dataHandler.enums.ProtectionType;
import com.musicUpload.dataHandler.models.implementations.Album;
import com.musicUpload.dataHandler.models.implementations.Song;
import com.musicUpload.dataHandler.models.implementations.User;
import com.musicUpload.dataHandler.repositories.AlbumRepository;
import com.musicUpload.dataHandler.repositories.SongRepository;
import com.musicUpload.dataHandler.repositories.UserRepository;
import com.musicUpload.exceptions.NotFoundException;
import com.musicUpload.exceptions.UnauthenticatedException;
import com.musicUpload.exceptions.UnprocessableException;
import com.musicUpload.exceptions.WrongFormatException;
import com.musicUpload.util.ImageFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final UserRepository userRepository;
    private final SongRepository songRepository;
    private final ImageFactory imageFactory;
    private final MinioService minioService;

    public AlbumDTO saveAlbum(String protectionType, String name, MultipartFile image) {
        UserDetailsImpl userDetails = UserService.getCurrentUserDetailsOrThrowError();

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
            album.setImage(imageFactory.getRandomImage(name));
        }

        var imageMap = minioService.getImageMap(album.getSongs().stream().map(Song::getImage).toList());

        return AlbumDTO.of(albumRepository.save(album), minioService.getImage(album.getImage()), imageMap);
    }

    public List<AlbumCardDTO> getAlbums() {
        User user = userRepository.findById(UserService.getCurrentUserDetailsOrThrowError().getId())
                .orElseThrow(UnauthenticatedException::new);

        List<Album> albums = albumRepository.findByUser(user);

        return albums.stream()
                .map(a -> AlbumCardDTO.of(a, minioService.getImage(a.getImage()))).toList();
    }

    public AlbumDTO findById(Long id) {
        UserDetailsImpl userDetails = UserService.getCurrentUserDetails();
        Album album = albumRepository.findById(id).orElseThrow(NotFoundException::new);
        if (!album.getProtectionType().equals(ProtectionType.PRIVATE) ||
                userDetails != null && album.getUser().getId().equals(userDetails.getId())) {
            return AlbumDTO.of(album, minioService.getImage(album.getImage()), minioService.getImageMap(album.getSongs().stream().map(Song::getImage).toList()));
        }
        throw new UnauthenticatedException();
    }

    public List<AlbumCardDTO> findByIdsIn(List<Long> ids) {
        User user = userRepository.findById(
                        Optional.ofNullable(UserService.getCurrentUserDetails())
                                .map(UserDetailsImpl::getId)
                                .orElse(-1L)
                ).orElseThrow(UnauthenticatedException::new);

        return albumRepository.findByIdInAndUserOrIdInAndProtectionType(ids, user.getId()).stream()
                .map(a -> AlbumCardDTO.of(a, minioService.getImage(a.getImage()))).toList();
    }

    public List<AlbumCardDTO> findByNameLike(String name, int pageNumber, int pageSize) {
        Long userId = UserService.getCurrentUserId();

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        return albumRepository.findByNameLike(name, userId, pageable).stream()
                .map(a -> AlbumCardDTO.of(a, minioService.getImage(a.getImage()))).toList();
    }

    public AlbumDTO patchAlbum(Long id, String protectionType, List<Long> songIds, String name, MultipartFile image) {
        UserDetailsImpl userDetails = UserService.getCurrentUserDetailsOrThrowError();

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
            List<Song> newSongs = songIds.stream().map(songId -> songRepository.findById(songId).map(song -> {
                if (!song.getProtectionType().equals(ProtectionType.PRIVATE)
                        || album.getUser().getSongs().stream()
                        .anyMatch(s -> s.getId().equals(song.getId()))) {
                    return song;
                }
                return null;
            }).orElse(null)).filter(Objects::nonNull).collect(Collectors.toCollection(ArrayList::new));
            album.setSongs(newSongs);
        }

        var imageMap = minioService.getImageMap(album.getSongs().stream().map(Song::getImage).toList());

        return AlbumDTO.of(albumRepository.save(album), minioService.getImage(album.getImage()), imageMap);
    }

    public AlbumDTO addSongs(Long id, List<Long> songIds) {
        UserDetailsImpl userDetails = UserService.getCurrentUserDetailsOrThrowError();

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(UnauthenticatedException::new);

        Album album = albumRepository.findByUserAndId(user, id)
                .orElseThrow(UnauthenticatedException::new);

        if (songIds != null) {
                songIds.forEach(songId -> songRepository
                    .findByIdAndProtectionTypeInOrUser(album.getId(), userDetails.getId())
                    .ifPresent(song -> album.getSongs().add(song)));
        }

        var minioImageMap = minioService.getImageMap(album.getSongs().stream().map(Song::getImage).toList());

        return AlbumDTO.of(albumRepository.save(album), minioService.getImage(album.getImage()), minioImageMap);
    }

    @Transactional
    public Album deleteAlbum(Long id) {
        UserDetailsImpl userDetails = UserService.getCurrentUserDetailsOrThrowError();

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(NotFoundException::new);

        Album album = albumRepository.findByUserAndId(user, id)
                .orElseThrow(UnauthenticatedException::new);

        user.getAlbums().remove(album);

        albumRepository.deleteById(album.getId());
        minioService.deleteImage(album.getImage());

        return album;
    }
}
