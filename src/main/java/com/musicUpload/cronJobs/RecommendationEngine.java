package com.musicUpload.cronJobs;

import com.musicUpload.dataHandler.enums.ProtectionType;
import com.musicUpload.dataHandler.models.implementations.Song;
import com.musicUpload.dataHandler.models.implementations.UserRecommendation;
import com.musicUpload.dataHandler.models.implementations.UserSong;
import com.musicUpload.dataHandler.repositories.SongRepository;
import com.musicUpload.dataHandler.repositories.UserRecommendationRepository;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class RecommendationEngine {
    private static final Logger logger = LogManager.getLogger(RecommendationEngine.class);
    private final UserSongService userSongService;
    private final SongRepository songRepository;
    private final UserRecommendationRepository userRecommendationRepository;

    @Autowired
    public RecommendationEngine(UserSongService userSongService, SongRepository songRepository, UserRecommendationRepository userRecommendationRepository) {
        this.userSongService = userSongService;
        this.songRepository = songRepository;
        this.userRecommendationRepository = userRecommendationRepository;
    }

    @Scheduled(cron = "00 * * * * *")
    public void createRecommendations() {
        Date start = Date.from(LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Set<UserSong> listens = userSongService.findByLastTwoMonths(start);
        Thread deletion = new Thread(userRecommendationRepository::deleteAll);
        deletion.start();
        logger.info("all connections from last month: {}", listens);
        //TODO: make this graph more efficient, maybe a general hashmap where we put every song with its connections and then filter it
        Set<GraphNode> graph = Collections.synchronizedSet(listens.stream().map(GraphNode::new).collect(Collectors.toSet()));
        graph.stream().parallel().forEach(node -> {
            Set<GraphNode> nodesWithSameSong = Collections.synchronizedSet(graph.stream()
                    .parallel()
                    .filter(n -> n.getUserSong().getSongId().equals(node.getUserSong().getSongId()))
                    .collect(Collectors.toSet()));
            node.setNodesWithSameSong(nodesWithSameSong);

            Set<GraphNode> nodesWithSameUser = Collections.synchronizedSet(graph.stream()
                    .parallel()
                    .filter(n -> n.getUserSong().getUserId().equals(node.getUserSong().getUserId()))
                    .collect(Collectors.toSet()));
            node.setNodesWithSameUser(nodesWithSameUser);
        });
        logger.info("graph node calculation finished");
        Map<Long, Set<Pair<Song, Long>>> userIdAndRecommendations = new ConcurrentHashMap<>();
        graph.stream().parallel().forEach(node -> {
            Long userId = node.getUserSong().getUserId();
            Set<GraphNode> nodesWithSameSong = node.getNodesWithSameSong();
            nodesWithSameSong.forEach(n -> {
                n.getNodesWithSameUser().stream()
                        .filter(s -> !s.getUserSong().getUserId().equals(userId))
                        .map(s -> s.getUserSong().getSongId())
                        .forEach(sId -> {
                            if (userIdAndRecommendations.containsKey(userId)) {
                                songRepository.findById(sId).ifPresent(song -> {
                                    var songPairOpt = userIdAndRecommendations.get(userId).stream().filter(p -> p.getFirst().equals(song)).findAny();
                                    if (song.getProtectionType().equals(ProtectionType.PUBLIC) || song.getUser().getId().equals(userId)) {
                                        songPairOpt.ifPresentOrElse(songPair -> songPair.setSecond(songPair.getSecond() + 1L),
                                                () -> userIdAndRecommendations.get(userId).add(Pair.of(song, 1L)));
                                    }
                                });
                            } else {
                                songRepository.findById(sId).ifPresent(song -> {
                                    if (song.getProtectionType().equals(ProtectionType.PUBLIC) || song.getUser().getId().equals(userId)) {
                                        userIdAndRecommendations.put(userId, new HashSet<>(Set.of(Pair.of(song, 1L))));

                                    }
                                });
                            }
                        });
            });
        });
        logger.info("recommendations: {}", userIdAndRecommendations);
        List<UserRecommendation> recommendations = Collections.synchronizedList(new ArrayList<>());
        userIdAndRecommendations.entrySet().stream().parallel().forEach(entry -> {
            List<Pair<Song, Long>> sortedList = entry.getValue().stream()
                    .sorted(Comparator.comparingLong(Pair::getSecond))
                    .limit(100)
                    .collect(Collectors.toCollection(ArrayList::new));

            IntStream.range(0, sortedList.size()).forEach(i -> recommendations.add(new UserRecommendation(
                    entry.getKey(),
                    sortedList.get(i).getFirst(),
                    i + 1)));
        });
        try {
            deletion.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        userRecommendationRepository.saveAll(recommendations);
        logger.info("recommendations calculated and saved in the db");
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString(exclude = {"nodesWithSameSong", "nodesWithSameUser"})
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
