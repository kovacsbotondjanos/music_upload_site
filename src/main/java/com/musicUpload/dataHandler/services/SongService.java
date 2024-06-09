package com.musicUpload.dataHandler.services;

import com.musicUpload.cronJobs.EntityCacheManager;
import com.musicUpload.cronJobs.SongCacheManager;
import com.musicUpload.dataHandler.DTOs.SongDTO;
import com.musicUpload.dataHandler.details.CustomUserDetails;
import com.musicUpload.dataHandler.models.implementations.ProtectionType;
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
import com.musicUpload.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class SongService {
    private static final Logger logger = LogManager.getLogger(SongService.class);
    private final String musicPathName = "music" + FileSystems.getDefault().getSeparator();
    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final AlbumRepository albumRepository;
    private final ImageFactory imageFactory;
    private final MusicFactory musicFactory;
    private final ProtectionTypeService protectionTypeService;
    private final SongCacheManager songCacheManager;
    private final EntityCacheManager<Song> entityManager;

    @Autowired
    public SongService(SongRepository songRepository, UserRepository userRepository, AlbumRepository albumRepository, ImageFactory imageFactory, MusicFactory songFactory, ProtectionTypeService protectionTypeService, SongCacheManager listenCountJob, EntityCacheManager<Song> entityManager) {
        this.songRepository = songRepository;
        this.userRepository = userRepository;
        this.albumRepository = albumRepository;
        this.imageFactory = imageFactory;
        this.musicFactory = songFactory;
        this.protectionTypeService = protectionTypeService;
        this.songCacheManager = listenCountJob;
        this.entityManager = entityManager;
    }

    public Song saveSong(Song song) {
        Song s = songRepository.save(song);
        songCacheManager.addSong(s);
        return s;
    }

    public Song saveSong(CustomUserDetails userDetails,
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

        ProtectionType protection = protectionTypeService.getProtectionTypeByName(protectionType)
                .orElseThrow(NotFoundException::new);
        song.setProtectionType(protection);

        song.setName(name);

        if (image != null && !image.isEmpty()) {
            try {
                if (!Objects.requireNonNull(image.getContentType()).contains("image")) {
                    throw new UnprocessableException();
                }
                String hashedFileName = UUID.randomUUID() + ".jpg";
                image.transferTo(new File(imageFactory.getDirName() + FileSystems.getDefault().getSeparator() + hashedFileName));
                imageFactory.deleteFile(song.getImage());
                song.setImage(hashedFileName);
            } catch (IOException ioException) {
                throw new UnprocessableException();
            }
        } else {
            String img = imageFactory.getRandomImage();
            song.setImage(img);
        }

        if (!songFile.isEmpty()) {
            try {
                if (!Objects.requireNonNull(songFile.getContentType()).contains("audio")) {
                    throw new UnprocessableException();
                }
                String hashedFileName = UUID.randomUUID() + ".mp3";
                songFile.transferTo(new File(musicFactory.getDirName() + FileSystems.getDefault().getSeparator() + hashedFileName));
                musicFactory.deleteFile(song.getNameHashed());
                song.setNameHashed(hashedFileName);
            } catch (IOException ioException) {
                throw new UnprocessableException();
            }
        }

        Song s = saveSong(song);
        userDetails.addSong(s);
        songCacheManager.addSong(s);
        return s;
    }

    public Optional<Song> findById(Long id) {
        Optional<Song> s = songCacheManager.getSong(id);
        s.ifPresent(__ -> logger.info("Song retrieved from cache"));
        if (s.isEmpty()) {
            //we only use this once, and if the opt is not empty we put it in the entityManager
            s = songRepository.findById(id);
        }
        return s;
    }

    public SongDTO findById(CustomUserDetails userDetails,
                            Long id) {
        Song song = findById(id)
                .orElseThrow(NotFoundException::new);
        songCacheManager.addSong(song);
        if (!song.getProtectionType().getName().equals("PRIVATE") ||
                userDetails != null && song.getUser().getId().equals(userDetails.getId())) {
            return SongDTO.of(song);
        }
        throw new UnauthenticatedException();
    }

    public List<SongDTO> getRandomSongs() {
        return songRepository.getRandomSongs().stream()
                .peek(songCacheManager::addSong)
                .map(SongDTO::new)
                .toList();
    }

    public List<SongDTO> getSongs(CustomUserDetails userDetails) {
        if (userDetails == null) {
            throw new UnauthenticatedException();
        }

        return userDetails.getSongs().stream().map(SongDTO::new).toList();
    }

    public List<SongDTO> findByNameLike(CustomUserDetails userDetails, String name) {
        List<Song> songs = songRepository.findByNameLike(name);
        if (userDetails == null) {
            return songs.stream()
                    .peek(songCacheManager::addSong)
                    .filter(s -> s.getProtectionType().getName().equals("PUBLIC")).limit(10)
                    .map(SongDTO::new)
                    .toList();
        }
        return songs.stream()
                .peek(songCacheManager::addSong)
                .filter(s -> s.getProtectionType().getName().equals("PUBLIC")
                        || s.getUser().getId().equals(userDetails.getId()))
                .limit(10)
                .map(SongDTO::new)
                .toList();
    }

    public Resource getSongInResourceFormatByNameHashed(CustomUserDetails userDetails, String nameHashed) {
        Path path = Paths.get(musicPathName);
        Song song = songRepository.findByNameHashed(nameHashed)
                .orElseThrow(NotFoundException::new);

        if (!song.getProtectionType().getName().equals("PRIVATE") ||
                userDetails != null && song.getUser().getId().equals(userDetails.getId())) {
            try {
                Path imagePath = path.resolve(song.getNameHashed());
                Resource resource = new UrlResource(imagePath.toUri());

                if (resource.exists()) {
                    songCacheManager.addListenToSong(song.getId(), userDetails);
                    songCacheManager.addSong(song);
                    return resource;
                } else {
                    throw new NotFoundException();
                }
            } catch (IOException e) {
                throw new NotFoundException();
            }
        }
        throw new NotFoundException();
    }

    public void updateSong(CustomUserDetails userDetails,
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
            Optional<ProtectionType> protectionOpt = protectionTypeService.getProtectionTypeByName(protectionType);
            protectionOpt.ifPresent(song::setProtectionType);
        }

        if (name != null) {
            song.setName(name);
        }

        if (image != null && !image.isEmpty()) {
            try {
                if (!Objects.requireNonNull(image.getContentType()).contains("image")) {
                    throw new WrongFormatException();
                }
                String hashedFileName = UUID.randomUUID() + ".jpg";
                image.transferTo(new File(imageFactory.getDirName() + FileSystems.getDefault().getSeparator() + hashedFileName));
                imageFactory.deleteFile(song.getImage());
                song.setImage(hashedFileName);
            } catch (IOException ioException) {
                throw new WrongFormatException();
            }
        }
        synchronized (songCacheManager.getCopyMap()) {
            logger.info("lock for song with id {} starts, because update", song.getId());
            songCacheManager.addSong(songRepository.save(song));
            logger.info("lock for song with id {} ends, because update", song.getId());
        }
    }

    public Song deleteSong(CustomUserDetails userDetails,
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

        synchronized (songCacheManager.getCopyMap()) {
            logger.info("lock for song with id {} starts, because deletion", song.getId());
            userDetails.getSongs().remove(song);
            songCacheManager.removeSong(song.getId());
            logger.info("lock for song with id {} ends, because deletion", song.getId());
        }

        return song;
    }
}
