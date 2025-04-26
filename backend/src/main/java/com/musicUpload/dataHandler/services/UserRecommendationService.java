package com.musicUpload.dataHandler.services;

import com.musicUpload.controllers.grpc.RecommendationServiceController;
import com.musicUpload.dataHandler.DTOs.SongDTO;
import com.musicUpload.dataHandler.details.UserDetailsImpl;
import com.musicUpload.dataHandler.enums.ProtectionType;
import com.musicUpload.dataHandler.repositories.SongRepository;
import com.musicUpload.recommendationEngine.grpc.client.Type;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRecommendationService {
    private final SongRepository songRepository;
    private final RecommendationServiceController recommendationServiceController;

    public UserRecommendationService(SongRepository songRepository,
                                     RecommendationServiceController recommendationServiceController) {
        this.songRepository = songRepository;
        this.recommendationServiceController = recommendationServiceController;
    }

    public List<SongDTO> getRecommendationsForSong(Long songId) {
        UserDetailsImpl userDetails = UserService.getCurrentUserDetails();
        long userId = userDetails != null ? userDetails.getId() : 0L;

        return songRepository.findByIdInAndUserOrIdInAndProtectionType(
                recommendationServiceController.getRecommendations(songId, userId, Type.SONG),
                userId,
                ProtectionType.PUBLIC
        ).stream().map(SongDTO::of).toList();
    }

    public List<SongDTO> getRecommendationsForAlbum(Long albumId) {
        UserDetailsImpl userDetails = UserService.getCurrentUserDetails();
        long userId = userDetails != null ? userDetails.getId() : 0L;

        return songRepository.findByIdInAndUserOrIdInAndProtectionType(
                recommendationServiceController.getRecommendations(albumId, userId, Type.ALBUM),
                userId,
                ProtectionType.PUBLIC
        ).stream().map(SongDTO::of).toList();
    }

    public List<SongDTO> getRecommendationsForUser() {
        UserDetailsImpl userDetails = UserService.getCurrentUserDetails();
        long userId = userDetails != null ? userDetails.getId() : 0L;
        return songRepository.findByIdInAndUserOrIdInAndProtectionType(
                recommendationServiceController.getRecommendations(userId),
                userId,
                ProtectionType.PUBLIC
        ).stream().map(SongDTO::of).toList();
    }
}
