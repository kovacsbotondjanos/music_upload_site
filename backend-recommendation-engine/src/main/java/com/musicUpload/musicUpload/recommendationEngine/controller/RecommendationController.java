package com.musicUpload.musicUpload.recommendationEngine.controller;

import com.musicUpload.musicUpload.recommendationEngine.recommendation.engine.RecommendationEngine;
import com.musicUpload.recommendationEngine.grpc.RecommendationRequest;
import com.musicUpload.recommendationEngine.grpc.RecommendationResponse;
import com.musicUpload.recommendationEngine.grpc.ServiceGrpc.ServiceImplBase;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;
import com.musicUpload.recommendationEngine.grpc.Type;

import java.util.List;

@GrpcService
@Slf4j
public class RecommendationController extends ServiceImplBase {

    private final RecommendationEngine recommendationEngine;

    public RecommendationController(RecommendationEngine recommendationEngine) {
        this.recommendationEngine = recommendationEngine;
    }

    @Override
    public void getLongList(RecommendationRequest request, StreamObserver<RecommendationResponse> responseObserver) {
        Long userId = request.getUserId();
        Long id = request.getId();
        Type type = request.getType();

        List<Long> recommendedIds = switch (type) {
            case USER -> recommendationEngine.createRecommendationsForUser(userId);
            case SONG -> recommendationEngine.createRecommendationsForSong(id, userId);
            case ALBUM -> recommendationEngine.createRecommendationsForAlbum(id, userId);
            case UNRECOGNIZED -> throw new IllegalArgumentException();
        };

        log.info("generated {} recommendation for type({}) and id({})", recommendedIds.size(), type, id);

        RecommendationResponse recommendationResponse = RecommendationResponse.newBuilder()
                .addAllIds(recommendedIds)
                .build();

        responseObserver.onNext(recommendationResponse);
        responseObserver.onCompleted();
    }
}