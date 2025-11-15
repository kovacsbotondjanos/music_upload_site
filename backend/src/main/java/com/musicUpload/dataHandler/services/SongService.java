package com.musicUpload.dataHandler.services;

import com.musicUpload.cronJobs.SongListenCountUpdateScheduler;
import com.musicUpload.dataHandler.DTOs.SongDAO;
import com.musicUpload.dataHandler.DTOs.SongDTO;
import com.musicUpload.dataHandler.details.UserDetailsImpl;
import com.musicUpload.dataHandler.enums.ProtectionType;
import com.musicUpload.dataHandler.models.implementations.Song;
import com.musicUpload.dataHandler.models.implementations.User;
import com.musicUpload.dataHandler.repositories.SongRepository;
import com.musicUpload.dataHandler.repositories.UserRepository;
import com.musicUpload.exceptions.NotFoundException;
import com.musicUpload.exceptions.UnauthenticatedException;
import com.musicUpload.exceptions.UnprocessableException;
import com.musicUpload.exceptions.WrongFormatException;
import com.musicUpload.util.ImageFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class SongService {
    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final ImageFactory imageFactory;
    private final MinioService minioService;
    private final SongListenCountUpdateScheduler songListenCountUpdateScheduler;
    private final TagService tagService;

    public Song addSong(SongDAO song, MultipartFile image, MultipartFile songFile) {
        return addSong(
                song.getProtectionType(), song.getName(), song.getTags(), image, songFile);
    }

    public Song addSong(
            String protectionType, String name, List<String> tags, MultipartFile image, MultipartFile songFile) {
        UserDetailsImpl userDetails = UserService.getCurrentUserDetailsOrThrowError();

        if (name == null || protectionType == null || songFile == null) {
            throw new WrongFormatException();
        }

        //we might assume that a user is authenticated and authorized at this point,
        //I'll might change this in the future, it might be overkill
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(UnauthenticatedException::new);

        Song song = new Song();
        song.setUser(user);

        song.setProtectionType(ProtectionType.valueOf(protectionType));

        song.setName(name);

        if (tags.size() > 3) {
            //when we have more than 3 tags we just get rid of the
            // rest, there's no need to throw an exception here
            tags = tags.stream().limit(3).toList();
        }

        song.addTags(tagService.findByIdsIn(tags));

        if (image != null && !image.isEmpty()) {
            if (!Objects.requireNonNull(image.getContentType()).contains("image")) {
                throw new UnprocessableException();
            }
            song.setImage(minioService.uploadImage(image));
        } else {
            song.setImage(imageFactory.getRandomImage(name));
        }

        if (!songFile.isEmpty()) {
            if (!Objects.requireNonNull(songFile.getContentType()).contains("audio")) {
                throw new UnprocessableException();
            }
            song.setNameHashed(minioService.uploadSong(songFile));
        }

        return songRepository.save(song);
    }

    public List<SongDTO> getSongs() {
        UserDetailsImpl userDetails = UserService.getCurrentUserDetailsOrThrowError();

        User u = userRepository.findById(userDetails.getId())
                .orElseThrow(NotFoundException::new);

        var songs = songRepository.findByUser(u);

        var songImageMap = minioService.getImageMap(songs.stream().map(Song::getImage).toList());

        return songs.stream().map(s -> SongDTO.of(s, songImageMap.get(s.getImage()))).toList();
    }

    public SongDTO findById(Long id) {
        UserDetailsImpl userDetails = UserService.getCurrentUserDetails();

        Song song = songRepository.findById(id).orElseThrow(NotFoundException::new);

        if (!song.getProtectionType().equals(ProtectionType.PRIVATE) ||
            userDetails != null && song.getUser().getId().equals(userDetails.getId())) {
            return SongDTO.of(song, minioService.getImage(song.getImage()));
        }
        throw new UnauthenticatedException();
    }

    public List<SongDTO> findByIdsIn(List<Long> ids) {
        UserDetailsImpl userDetails = UserService.getCurrentUserDetails();

        Long userId = userDetails != null ? userDetails.getId() : 0L;

        var songs = songRepository.findByIdInAndUserOrIdInAndProtectionType(ids, userId);

        var songImageMap = minioService.getImageMap(songs.stream().map(Song::getImage).toList());

        return songs.stream().map(s -> SongDTO.of(s, songImageMap.get(s.getImage()))).toList();
    }

    public List<SongDTO> findByNameLike(String name, int pageNumber, int pageSize) {
        Long userId = UserService.getCurrentUserId();

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        var songs = songRepository.findByNameLike(name, userId, pageable);

        var songImageMap = minioService.getImageMap(songs.stream().map(Song::getImage).toList());

        return songs.stream().map(s -> SongDTO.of(s, songImageMap.get(s.getImage()))).toList();
    }

    public String getSong(String nameHashed) {
        UserDetailsImpl userDetails = UserService.getCurrentUserDetails();

        Song song = songRepository.findByNameHashed(nameHashed)
                .orElseThrow(NotFoundException::new);

        if (!song.getProtectionType().equals(ProtectionType.PRIVATE) ||
                userDetails != null && song.getUser().getId().equals(userDetails.getId())) {
            songListenCountUpdateScheduler.addListenToSong(song.getId(), userDetails);
            return minioService.getSong(nameHashed);
        }
        throw new NotFoundException();
    }

    @Transactional
    public void patchSong(Long id, SongDAO song, MultipartFile image) {
        patchSong(id, song.getProtectionType(), song.getName(), song.getTags(), image);
    }

    public void patchSong(Long id, String protectionType, String name, List<String> tags, MultipartFile image) {
        UserDetailsImpl userDetails = UserService.getCurrentUserDetailsOrThrowError();

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(UnauthenticatedException::new);

        Song song = songRepository.findByUserAndId(user, id)
                .orElseThrow(UnauthenticatedException::new);

        if (protectionType != null) {
            song.setProtectionType(ProtectionType.valueOf(protectionType));
        }

        if (tags != null && !tags.isEmpty()) {
            song.setTags(new HashSet<>(tagService.findByIdsIn(tags)));
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

        songRepository.save(song);
    }

    @Transactional
    public Song deleteSong(Long id) {
        UserDetailsImpl userDetails = UserService.getCurrentUserDetailsOrThrowError();

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(NotFoundException::new);

        Song song = songRepository.findByUserAndId(user, id)
                .orElseThrow(UnauthenticatedException::new);

        user.getSongs().remove(song);

        song.getAlbums().forEach(a -> a.getSongs().remove(song));

        songRepository.deleteById(id);
        minioService.deleteImage(song.getImage());
        minioService.deleteSong(song.getNameHashed());

        return song;
    }
}
