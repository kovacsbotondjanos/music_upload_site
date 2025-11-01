package com.musicUpload.musicUpload.recommendationEngine.controller;

import com.musicUpload.musicUpload.recommendationEngine.database.service.RecommendationEngine;
import com.musicUpload.recommendationEngine.grpc.RecommendationRequest;
import com.musicUpload.recommendationEngine.grpc.RecommendationResponse;
import com.musicUpload.recommendationEngine.grpc.ServiceGrpc.ServiceImplBase;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;
import com.musicUpload.recommendationEngine.grpc.Type;

import java.util.List;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class RecommendationController extends ServiceImplBase {

    private final RecommendationEngine recommendationEngine;

    @Override
    public void getLongList(RecommendationRequest request, StreamObserver<RecommendationResponse> responseObserver) {
        Long userId = request.getUserId();
        Long id = request.getId();
        Type type = request.getType();
        Long pageSize = request.getPageSize();
        Long pageNumber = request.getPageNumber();

        List<Long> recommendedIds = switch (type) {
            case USER -> recommendationEngine.createRecommendationsForUser(userId, pageSize, pageNumber);
            case SONG -> recommendationEngine.createRecommendationsForSong(id, userId, pageSize, pageNumber);
            case ALBUM -> recommendationEngine.createRecommendationsForAlbum(id, userId, pageSize, pageNumber);
            case UNRECOGNIZED -> throw new IllegalArgumentException();
        };

        RecommendationResponse recommendationResponse = RecommendationResponse.newBuilder()
                .addAllIds(recommendedIds)
                .build();

        responseObserver.onNext(recommendationResponse);
        responseObserver.onCompleted();
    }
}