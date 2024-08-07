package com.musicUpload.dataHandler.services;

import com.musicUpload.cronJobs.SongCacheManager;
import com.musicUpload.dataHandler.DTOs.SongDTO;
import com.musicUpload.dataHandler.details.UserDetailsImpl;
import com.musicUpload.dataHandler.enums.ProtectionType;
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
import com.musicUpload.util.MusicFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class SongService {
    private static final Logger logger = LogManager.getLogger(SongService.class);
    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final AlbumRepository albumRepository;
    private final ImageFactory imageFactory;
    private final MusicFactory musicFactory;
    private final SongCacheManager songCacheManager;
    private final UserRecommendationService userRecommendationService;
    private final MinioService minioService;

    @Autowired
    public SongService(SongRepository songRepository,
                       UserRepository userRepository,
                       AlbumRepository albumRepository,
                       ImageFactory imageFactory,
                       MusicFactory songFactory,
                       SongCacheManager songCacheManager,
                       UserRecommendationService userRecommendationService,
                       MinioService minioService) {
        this.songRepository = songRepository;
        this.userRepository = userRepository;
        this.albumRepository = albumRepository;
        this.imageFactory = imageFactory;
        this.musicFactory = songFactory;
        //TODO: create a wrapper class to handle lookup in the cache and db too
        this.songCacheManager = songCacheManager;
        this.userRecommendationService = userRecommendationService;
        this.minioService = minioService;
    }

    public Song addSong(Song song) {
        Song s = songRepository.save(song);
        songCacheManager.addSong(s);
        return s;
    }

    public Song addSong(UserDetailsImpl userDetails,
                        String protectionType,
                        String name,
                        MultipartFile image,
                        MultipartFile songFile) {
        if (userDetails == null) {
            throw new UnauthenticatedException();
        }

        if (name == null || protectionType == null || songFile == null) {
            throw new WrongFormatException();
        }

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(UnauthenticatedException::new);

        Song song = new Song();
        song.setUser(user);

        song.setProtectionType(ProtectionType.getByName(protectionType));

        song.setName(name);

        if (image != null && !image.isEmpty()) {
            if (!Objects.requireNonNull(image.getContentType()).contains("image")) {
                throw new UnprocessableException();
            }
            song.setImage(minioService.uploadImage(image));
        } else {
            String img = imageFactory.getRandomImage();
            song.setImage(img);
        }

        if (!songFile.isEmpty()) {
            if (!Objects.requireNonNull(songFile.getContentType()).contains("audio")) {
                throw new UnprocessableException();
            }
            song.setNameHashed(minioService.uploadSong(songFile));
        }

        Song s = addSong(song);
        userDetails.addSong(s);
        return s;
    }

    public List<SongDTO> getSongs(UserDetailsImpl userDetails) {
        if (userDetails == null) {
            throw new UnauthenticatedException();
        }

        return userDetails.getSongs().stream().map(SongDTO::new).toList();
    }

    public Optional<Song> findById(Long id) {
        Optional<Song> s = songCacheManager.getSong(id);
        if (s.isEmpty()) {
            //we only use this once, and if the opt is not empty we put it in the entityManager
            s = songRepository.findById(id);
        }
        return s;
    }

    public SongDTO findById(UserDetailsImpl userDetails,
                            Long id) {
        Song song = findById(id)
                .orElseThrow(NotFoundException::new);
        songCacheManager.addSong(song);
        if (!song.getProtectionType().equals(ProtectionType.PRIVATE) ||
                userDetails != null && song.getUser().getId().equals(userDetails.getId())) {
            return SongDTO.of(song);
        }
        throw new UnauthenticatedException();
    }

    public List<SongDTO> getRecommendedSongs(UserDetailsImpl userDetails,
                                             int pageNumber,
                                             int pageSize) {
        Pageable page = PageRequest.of(pageNumber, pageSize);

        if (userDetails != null) {
            return userRecommendationService.getRecommendedSongsForUser(userDetails, pageNumber, pageSize);
        }
        return songRepository.findByProtectionTypeOrderByListenCountDesc(ProtectionType.PUBLIC, page).stream()
                .peek(songCacheManager::addSong)
                .map(SongDTO::new)
                .toList();
    }

    public List<SongDTO> findByNameLike(UserDetailsImpl userDetails,
                                        String name,
                                        int pageNumber,
                                        int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        //TODO: pagination!!!
        List<Song> songs = songRepository.findByNameLike(name);
        if (userDetails == null) {
            return songs.stream()
                    .peek(songCacheManager::addSong)
                    .filter(s -> s.getProtectionType().equals(ProtectionType.PUBLIC)).limit(10)
                    .map(SongDTO::new)
                    .toList();
        }
        return songs.stream()
                .peek(songCacheManager::addSong)
                .filter(s -> s.getProtectionType().equals(ProtectionType.PUBLIC)
                        || s.getUser().getId().equals(userDetails.getId()))
                .limit(10)
                .map(SongDTO::new)
                .toList();
    }

    public String getSong(UserDetailsImpl userDetails, String nameHashed) {
        Song song = songRepository.findByNameHashed(nameHashed)
                .orElseThrow(NotFoundException::new);

        if (!song.getProtectionType().equals(ProtectionType.PRIVATE) ||
                userDetails != null && song.getUser().getId().equals(userDetails.getId())) {
            songCacheManager.addListenToSong(song.getId(), userDetails);
            songCacheManager.addSong(song);
            return minioService.getSong(nameHashed);
        }
        throw new NotFoundException();
    }

    public void patchSong(UserDetailsImpl userDetails,
                          Long id,
                          String protectionType,
                          String name,
                          MultipartFile image) {
        if (userDetails == null) {
            throw new UnauthenticatedException();
        }

        Song song = userDetails.getSongs().stream().filter(s -> s.getId().equals(id)).findAny()
                .orElseThrow(UnauthenticatedException::new);

        if (protectionType != null) {
            song.setProtectionType(ProtectionType.getByName(protectionType));
        }

        if (name != null) {
            song.setName(name);
        }

        if (image != null && !image.isEmpty()) {
            if (!Objects.requireNonNull(image.getContentType()).contains("image")) {
                throw new WrongFormatException();
            }
            minioService.deleteImage(song.getImage());
            song.setImage(minioService.uploadImage(image));
        }
        synchronized (songCacheManager.getCopyMap()) {
            logger.info("lock for song with id {} starts, because update", song.getId());
            songCacheManager.addSong(songRepository.save(song));
            logger.info("lock for song with id {} ends, because update", song.getId());
        }
    }

    public Song deleteSong(UserDetailsImpl userDetails,
                           Long id) {
        if (userDetails == null) {
            throw new UnauthenticatedException();
        }

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(NotFoundException::new);

        Song song = user.getSongs().stream()
                .filter(s -> s.getId().equals(id))
                .findAny()
                .orElseThrow(UnauthenticatedException::new);

        user.getSongs().remove(song);
        userRepository.save(user);

        song.getAlbums().forEach(a -> {
            a.getSongs().remove(song);
            albumRepository.save(a);
        });

        songRepository.delete(song);
        minioService.deleteImage(song.getImage());
        minioService.deleteSong(song.getNameHashed());

        synchronized (songCacheManager.getCopyMap()) {
            logger.info("lock for song with id {} starts, because deletion", song.getId());
            userDetails.getSongs().remove(song);
            songCacheManager.removeSong(song.getId());
            logger.info("lock for song with id {} ends, because deletion", song.getId());
        }

        return song;
    }
}
