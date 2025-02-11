package com.musicUpload.dataHandler.services;

import com.musicUpload.dataHandler.DTOs.SongDTO;
import com.musicUpload.dataHandler.details.UserDetailsImpl;
import com.musicUpload.dataHandler.enums.ProtectionType;
import com.musicUpload.dataHandler.models.implementations.UserRecommendation;
import com.musicUpload.dataHandler.repositories.SongRepository;
import com.musicUpload.dataHandler.repositories.UserRecommendationRepository;
import com.musicUpload.exceptions.UnauthenticatedException;
import com.musicUpload.recommendation.RecommendationEngine;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserRecommendationService {
    private final UserRecommendationRepository userRecommendationRepository;
    private final SongRepository songRepository;
    private final RecommendationEngine recommendationEngine;

    public UserRecommendationService(UserRecommendationRepository userRecommendationRepository,
                                     SongRepository songRepository,
                                     RecommendationEngine recommendationEngine) {
        this.userRecommendationRepository = userRecommendationRepository;
        this.songRepository = songRepository;
        this.recommendationEngine = recommendationEngine;
    }

    public List<Long> getRecommendationsForSong(Long songId) {
        return recommendationEngine.createRecommendationsForSong(songId);
    }

    public List<Long> getRecommendationsForAlbum(Long albumId) {
        return recommendationEngine.createRecommendationsForAlbum(albumId);
    }

    public List<Long> getRecommendationsForUser() {
        return recommendationEngine.createRecommendationsForUser(
                UserService.getCurrentUserDetails().getId()
        );
    }

    public List<SongDTO> getRecommendedSongsForUser(UserDetailsImpl userDetails,
                                                    int pageNumber,
                                                    int pageSize) {
        if (userDetails == null) {
            throw new UnauthenticatedException();
        }

        Pageable p = PageRequest.of(pageNumber, pageSize);
        List<SongDTO> recommendations = userRecommendationRepository
                .findByUserIdOrderByRecommendationPosition(userDetails.getId(), p)
                .stream()
                .map(UserRecommendation::getSong)
                .filter(s -> !s.getProtectionType().equals(ProtectionType.PRIVATE))
                .map(SongDTO::of)
                .collect(Collectors.toCollection(ArrayList::new));

        if (recommendations.size() < pageSize) {
            p = PageRequest.of(0, pageSize - recommendations.size());
            recommendations.addAll(songRepository.findByProtectionTypeOrderByListenCountDesc(ProtectionType.PUBLIC, p).stream()
                    .map(SongDTO::new)
                    .toList());
        }

        return recommendations;
    }
}
