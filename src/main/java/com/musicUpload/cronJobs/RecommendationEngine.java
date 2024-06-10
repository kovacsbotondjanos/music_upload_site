package com.musicUpload.cronJobs;

import com.musicUpload.dataHandler.models.implementations.User;
import com.musicUpload.dataHandler.models.implementations.UserSong;
import com.musicUpload.dataHandler.services.UserService;
import com.musicUpload.dataHandler.services.UserSongService;
import com.musicUpload.util.Pair;
import lombok.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationEngine {
    private static final Logger logger = LogManager.getLogger(RecommendationEngine.class);
    private final UserSongService userSongService;

    @Autowired
    public RecommendationEngine(UserSongService userSongService) {
        this.userSongService = userSongService;
    }

    //TODO: set this to midnight when done testing
    @Scheduled(cron = "00 * * * * *")
    public void createRecommendations() {
        Date start = Date.from(LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Set<UserSong> listens = userSongService.findByLastTwoMonths(start);
        logger.info("all connections from last two months: {}", listens);
        Set<GraphNode> graph = listens.stream().map(GraphNode::new).collect(Collectors.toSet());
        graph.forEach(node -> {
            Set<GraphNode> nodesWithSameSong = graph.stream()
                    .filter(n -> n.getUserSong().getSongId().equals(node.getUserSong().getSongId()))
                    .collect(Collectors.toSet());
            node.setNodesWithSameSong(nodesWithSameSong);

            Set<GraphNode> nodesWithSameUser = graph.stream()
                    .filter(n -> n.getUserSong().getUserId().equals(node.getUserSong().getUserId()))
                    .collect(Collectors.toSet());
            node.setNodesWithSameUser(nodesWithSameUser);
        });
        Map<Long, Set<Pair<Long, Long>>> userIdAndRecommendations = new HashMap<>();
        graph.forEach(node -> {
            Long userId = node.getUserSong().getUserId();
            Long songId = node.getUserSong().getSongId();
            Set<GraphNode> nodesWithSameSong = graph.stream()
                    .filter(n -> n.getUserSong().getSongId().equals(songId))
                    .collect(Collectors.toSet());
            nodesWithSameSong.forEach(n -> {
                n.getNodesWithSameUser().stream()
                        .map(s -> s.getUserSong().getSongId())
                        .forEach(sId -> {
                            if(userIdAndRecommendations.containsKey(userId)) {
                                var songPairOpt = userIdAndRecommendations.get(userId).stream().filter(p -> p.getFirst().equals(sId)).findAny();
                                songPairOpt.ifPresentOrElse(songPair -> songPair.setSecond(songPair.getSecond() + 1L),
                                                            () -> userIdAndRecommendations.get(userId).add(new Pair<>(sId, 1L)));
                            }
                            else {
                                userIdAndRecommendations.put(userId, new HashSet<>(Set.of(new Pair<>(sId, 1L))));
                            }
                        });
            });
        });
        logger.info("recommendations: {}", userIdAndRecommendations);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    @EqualsAndHashCode(exclude = {"nodesWithSameSong", "nodesWithSameUser"})
    static class GraphNode {
        private UserSong userSong;
        private Set<GraphNode> nodesWithSameSong;
        private Set<GraphNode> nodesWithSameUser;

        public GraphNode(UserSong userSong) {
            this.userSong = userSong;
        }
    }
}
