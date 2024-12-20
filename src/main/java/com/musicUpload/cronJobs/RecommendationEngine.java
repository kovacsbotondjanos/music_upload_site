package com.musicUpload.cronJobs;

import com.musicUpload.dataHandler.models.implementations.Album;
import com.musicUpload.dataHandler.models.implementations.Song;
import com.musicUpload.dataHandler.models.implementations.UserSong;
import com.musicUpload.dataHandler.repositories.AlbumRepository;
import com.musicUpload.dataHandler.repositories.SongRepository;
import com.musicUpload.dataHandler.repositories.UserRecommendationRepository;
import com.musicUpload.dataHandler.repositories.UserRepository;
import com.musicUpload.dataHandler.services.AlbumService;
import com.musicUpload.dataHandler.services.UserSongService;
import com.musicUpload.exceptions.NotFoundException;
import com.musicUpload.exceptions.UnauthenticatedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class RecommendationEngine {
    private final UserSongService userSongService;
    private final SongRepository songRepository;
    private final UserRecommendationRepository userRecommendationRepository;
    private final UserRepository userRepository;
    private final AlbumRepository albumRepository;

    @Autowired
    public RecommendationEngine(UserSongService userSongService,
                                SongRepository songRepository,
                                UserRecommendationRepository userRecommendationRepository,
                                UserRepository userRepository,
                                AlbumRepository albumRepository) {
        this.userSongService = userSongService;
        this.songRepository = songRepository;
        this.userRecommendationRepository = userRecommendationRepository;
        this.userRepository = userRepository;
        this.albumRepository = albumRepository;
    }

    private Map<Long, Long> getSongsWithOccurrenceCount(Long songId) {
        Set<UserSong> userSongList = userSongService.getListensForSong(songId);
        Set<UserSong> songsForUsers = userSongService.getSongsForUsers(
                userSongList
                        .stream()
                        .parallel()
                        .map(UserSong::getUserId)
                        .collect(Collectors.toSet())
        );

        Map<Long, Song> alreadyQueriedSongs = new ConcurrentHashMap<>();
        Map<Long, Long> songOccurrenceMap = new ConcurrentHashMap<>();
        songsForUsers.stream().parallel()
                .forEach(userSong -> {
                    var song = alreadyQueriedSongs.get(userSong.getSongId());

                    if (song == null) {
                        song = songRepository.findById(userSong.getSongId()).orElseGet(null);
                        if (song != null) {
                            alreadyQueriedSongs.put(songId, song);
                        }
                    }

                    if (song != null) {
                        songOccurrenceMap.merge(song.getId(), 1L, Long::sum);
                    }
                });

        return songOccurrenceMap;
    }

    public List<Map.Entry<Long, Long>> createRecommendationsForSong(Long songId) {
        return getSongsWithOccurrenceCount(songId)
                .entrySet()
                .stream()
                .sorted(Comparator.comparingLong(Map.Entry::getValue))
                .toList();
    }

    public List<Long> createRecommendationsForUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(UnauthenticatedException::new);

        Set<UserSong> songs = userSongService.getSongsForUser(userId);

        Map<Long, Long> songOccurrences = new ConcurrentHashMap<>();
        songs.stream().parallel().forEach(userSong -> {
            Long songId = userSong.getSongId();
            var map = getSongsWithOccurrenceCount(songId);
            songOccurrences.merge(songId, map.get(songId), Long::sum);
        });

        return songOccurrences
                .entrySet()
                .stream()
                .sorted(Comparator.comparingLong(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .toList();
    }

    public List<Long> createRecommendationsForAlbum(Long albumId) {
        Album a = albumRepository.findById(albumId)
                .orElseThrow(NotFoundException::new);

        List<Song> songs = a.getSongs();

        Map<Long, Long> songOccurrences = new ConcurrentHashMap<>();
        songs.stream().parallel().forEach(song -> {
            Long songId = song.getId();
            var map = getSongsWithOccurrenceCount(songId);
            songOccurrences.merge(songId, map.get(songId), Long::sum);
        });

        return songOccurrences
                .entrySet()
                .stream()
                .sorted(Comparator.comparingLong(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .toList();
    }
}
