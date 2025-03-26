package com.musicUpload.dataHandler.services;

import com.musicUpload.dataHandler.DTOs.SongDTO;
import com.musicUpload.dataHandler.details.UserDetailsImpl;
import com.musicUpload.dataHandler.enums.ProtectionType;
import com.musicUpload.dataHandler.models.implementations.Recommendation;
import com.musicUpload.dataHandler.repositories.RecommendationRepository;
import com.musicUpload.dataHandler.repositories.SongRepository;
import com.musicUpload.recommendation.RecommendationEngine;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserRecommendationService {
    private final SongRepository songRepository;
    private final RecommendationEngine recommendationEngine;
    private final RecommendationRepository recommendationRepository;

    public UserRecommendationService(SongRepository songRepository,
                                     RecommendationEngine recommendationEngine,
                                     RecommendationRepository recommendationRepository) {
        this.songRepository = songRepository;
        this.recommendationEngine = recommendationEngine;
        this.recommendationRepository = recommendationRepository;
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

        if (userId != 0L && checkLastCachedRecommendations(recommendationRepository.findLastDate(userId))) {
            return getSavedRecommendations(userId);

        } else if (userId != 0L) {
            recommendationRepository.deleteByUser(userId);
        }

        List<Long> userRecommendations = recommendationEngine.createRecommendationsForUser(userId);

        if (userId != 0L) {
            new Thread(() ->
                    recommendationRepository.saveAll(
                            userRecommendations.stream()
                                    .map(rec -> new Recommendation(rec, userId))
                                    .toList()
                    )).start();
        }

        return songRepository.findByIdInAndUserOrIdInAndProtectionType(
                userRecommendations,
                userId,
                ProtectionType.PUBLIC
        ).stream().map(SongDTO::of).toList();
    }

    private boolean checkLastCachedRecommendations(Optional<Date> lastDate) {
        if (lastDate.isEmpty()) {
            return false;
        }

        LocalDateTime lastUpdateDateTime = lastDate.map(date -> date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()).orElseThrow();

        return ChronoUnit.MINUTES.between(
                lastUpdateDateTime,
                LocalDateTime.now()
        ) < 30;
    }

    private List<SongDTO> getSavedRecommendations(Long userId) {
        return songRepository.findByIdInAndUserOrIdInAndProtectionType(
                recommendationRepository.findByUser(userId).stream().map(Recommendation::getSongId).toList(),
                userId,
                ProtectionType.PUBLIC
        ).stream().map(SongDTO::of).toList();
    }
}
