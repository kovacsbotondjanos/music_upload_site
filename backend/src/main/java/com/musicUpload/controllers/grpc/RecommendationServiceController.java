package com.musicUpload.controllers.grpc;

import com.musicUpload.recommendationEngine.grpc.client.ServiceGrpc;
import com.musicUpload.recommendationEngine.grpc.client.RecommendationRequest;
import com.musicUpload.recommendationEngine.grpc.client.Type;
import com.musicUpload.util.CacheUtils;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class RecommendationServiceController {

    private final ServiceGrpc.ServiceBlockingStub blockingStub;

    public RecommendationServiceController(@Value("${GRPC_RECOMMENDATION_SERVER_HOST:localhost}") String recommendationHost,
                                           @Value("${GRPC_RECOMMENDATION_SERVER_PORT:9000}") int recommendationPort) {
        blockingStub = ServiceGrpc.newBlockingStub(
                ManagedChannelBuilder.forAddress(recommendationHost, recommendationPort)
                        .usePlaintext()
                        .build()
        );
    }

    @PostConstruct
    public void warmUp() {
        CompletableFuture.runAsync(() -> {
            try {
                log.debug("Dummy call to GRPC server in order for the first real call to be faster");
                blockingStub.getLongList(RecommendationRequest.newBuilder()
                        .setId(0)
                        .setUserId(0)
                        .setType(Type.SONG)
                        .setPageNumber(0)
                        .setPageSize(0)
                        .build());
                log.debug("Dummy call finished");
            } catch (Exception e) {
                log.warn("gRPC call failed: {}", e.getMessage());
            }
        });
    }

    @Cacheable(value = CacheUtils.SONG_RECOMMENDATION, key = "{#id, #userId, #pageSize, #pageNumber}")
    public List<Long> getRecommendationsForSong(long id, long userId, long pageSize, long pageNumber) {
        return getRecommendations(id, userId, Type.SONG, pageSize, pageNumber);
    }

    @Cacheable(value = CacheUtils.ALBUM_RECOMMENDATION, key = "{#id, #userId, #pageSize, #pageNumber}")
    public List<Long> getRecommendationsForAlbum(long id, long userId, long pageSize, long pageNumber) {
        return getRecommendations(id, userId, Type.ALBUM, pageSize, pageNumber);
    }

    @Cacheable(value = CacheUtils.USER_RECOMMENDATION, key = "{#userId, #pageSize, #pageNumber}")
    public List<Long> getRecommendationsForUser(long userId, long pageSize, long pageNumber) {
        return getRecommendations(userId, userId, Type.USER, pageSize, pageNumber);
    }

    private List<Long> getRecommendations(long id, long userId, Type type, long pageSize, long pageNumber) {
        RecommendationRequest request = RecommendationRequest.newBuilder()
                .setId(id)
                .setUserId(userId)
                .setType(type)
                .setPageNumber(pageNumber)
                .setPageSize(pageSize)
                .build();
        return blockingStub.getLongList(request).getIdsList();
    }
}
