package com.musicUpload.dataHandler.services;

import com.musicUpload.controllers.grpc.RecommendationServiceController;
import com.musicUpload.dataHandler.DTOs.SongDTO;
import com.musicUpload.dataHandler.details.UserDetailsImpl;
import com.musicUpload.dataHandler.models.implementations.Song;
import com.musicUpload.dataHandler.repositories.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRecommendationService {
    private final SongRepository songRepository;
    private final RecommendationServiceController recommendationServiceController;
    private final MinioService minioService;

    public List<SongDTO> getRecommendationsForSong(Long songId, Long pageSize, Long pageNumber) {
        UserDetailsImpl userDetails = UserService.getCurrentUserDetails();
        long userId = userDetails != null ? userDetails.getId() : 0L;
        return getRecommendationsForSong(songId, userId, pageSize, pageNumber);
    }

    public List<SongDTO> getRecommendationsForAlbum(Long albumId, Long pageSize, Long pageNumber) {
        UserDetailsImpl userDetails = UserService.getCurrentUserDetails();
        long userId = userDetails != null ? userDetails.getId() : 0L;
        return getRecommendationsForAlbum(albumId, userId, pageSize, pageNumber);
    }

    public List<SongDTO> getRecommendationsForUser(Long pageSize, Long pageNumber) {
        UserDetailsImpl userDetails = UserService.getCurrentUserDetails();
        long userId = userDetails != null ? userDetails.getId() : 0L;
        return getRecommendationsForUser(userId, pageSize, pageNumber);
    }

    private List<SongDTO> getRecommendationsForSong(Long songId, Long userId, Long pageSize, Long pageNumber) {
        var songs = songRepository.findByIdInAndUserOrIdInAndProtectionType(
                recommendationServiceController.getRecommendationsForSong(songId, userId, pageSize, pageNumber), userId);
        var songImageMap = minioService.getImageMap(songs.stream().map(Song::getImage).toList());
        return songs.stream().map(s -> SongDTO.of(s, songImageMap.get(s.getImage()))).toList();
    }

    private List<SongDTO> getRecommendationsForAlbum(Long albumId, Long userId, Long pageSize, Long pageNumber) {
        var songs = songRepository.findByIdInAndUserOrIdInAndProtectionType(
                recommendationServiceController.getRecommendationsForAlbum(albumId, userId, pageSize, pageNumber), userId);
        var songImageMap = minioService.getImageMap(songs.stream().map(Song::getImage).toList());
        return songs.stream().map(s -> SongDTO.of(s, songImageMap.get(s.getImage()))).toList();
    }

    private List<SongDTO> getRecommendationsForUser(Long userId, Long pageSize, Long pageNumber) {
        var songs = songRepository.findByIdInAndUserOrIdInAndProtectionType(
                recommendationServiceController.getRecommendationsForUser(userId, pageSize, pageNumber), userId);
        var songImageMap = minioService.getImageMap(songs.stream().map(Song::getImage).toList());
        return songs.stream().map(s -> SongDTO.of(s, songImageMap.get(s.getImage()))).toList();
    }
}
