package com.musicUpload.dataHandler.services;

import com.musicUpload.dataHandler.DTOs.SongDTO;
import com.musicUpload.dataHandler.details.UserDetailsImpl;
import com.musicUpload.dataHandler.enums.ProtectionType;
import com.musicUpload.dataHandler.repositories.SongRepository;
import com.musicUpload.recommendation.RecommendationEngine;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRecommendationService {
    private final SongRepository songRepository;
    private final RecommendationEngine recommendationEngine;

    public UserRecommendationService(SongRepository songRepository,
                                     RecommendationEngine recommendationEngine) {
        this.songRepository = songRepository;
        this.recommendationEngine = recommendationEngine;
    }

    public List<SongDTO> getRecommendationsForSong(Long songId) {
        UserDetailsImpl userDetails = UserService.getCurrentUserDetails();
        Long userId = userDetails != null ? userDetails.getId() : 0L;

        return songRepository.findByIdInAndUserOrIdInAndProtectionType(
                recommendationEngine.createRecommendationsForSong(songId),
                userId,
                ProtectionType.PUBLIC
        ).stream().map(SongDTO::of).toList();
    }

    public List<SongDTO> getRecommendationsForAlbum(Long albumId) {
        UserDetailsImpl userDetails = UserService.getCurrentUserDetails();
        Long userId = userDetails != null ? userDetails.getId() : 0L;

        return songRepository.findByIdInAndUserOrIdInAndProtectionType(
                recommendationEngine.createRecommendationsForAlbum(albumId),
                userId,
                ProtectionType.PUBLIC
        ).stream().map(SongDTO::of).toList();
    }

    public List<SongDTO> getRecommendationsForUser() {
        UserDetailsImpl userDetails = UserService.getCurrentUserDetails();
        Long userId = userDetails != null ? userDetails.getId() : 0L;
        return songRepository.findByIdInAndUserOrIdInAndProtectionType(
                recommendationEngine.createRecommendationsForUser(userId),
                userId,
                ProtectionType.PUBLIC
        ).stream().map(SongDTO::of).toList();
    }
}
