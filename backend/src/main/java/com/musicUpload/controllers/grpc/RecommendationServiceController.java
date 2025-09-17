package com.musicUpload.controllers.grpc;

import com.musicUpload.recommendationEngine.grpc.client.ServiceGrpc;
import com.musicUpload.recommendationEngine.grpc.client.RecommendationRequest;
import com.musicUpload.recommendationEngine.grpc.client.Type;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class RecommendationServiceController {

    private final ManagedChannel channel;
    private final ServiceGrpc.ServiceBlockingStub blockingStub;

    public RecommendationServiceController(@Value("${GRPC_RECOMMENDATION_SERVER_HOST:localhost}") String recommendationHost,
                                           @Value("${GRPC_RECOMMENDATION_SERVER_PORT:9000}") int recommendationPort) {
        channel = ManagedChannelBuilder.forAddress(recommendationHost, recommendationPort)
                .usePlaintext()
                .build();
        blockingStub = ServiceGrpc.newBlockingStub(channel);
    }

    public List<Long> getRecommendations(long id, long userId, Type type) {
        RecommendationRequest request = RecommendationRequest.newBuilder()
                .setId(id)
                .setUserId(userId)
                .setType(type)
                .build();
        return blockingStub.getLongList(request).getIdsList();
    }

    public List<Long> getRecommendations(long userId) {
        return getRecommendations(userId, userId, Type.USER);
    }
}
