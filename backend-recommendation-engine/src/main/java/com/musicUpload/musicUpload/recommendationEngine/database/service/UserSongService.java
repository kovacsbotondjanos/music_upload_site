package com.musicUpload.musicUpload.recommendationEngine.database.service;

import com.musicUpload.musicUpload.recommendationEngine.database.entity.UserSong;
import com.musicUpload.musicUpload.recommendationEngine.database.repository.SongRepository;
import com.musicUpload.musicUpload.recommendationEngine.database.repository.UserSongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserSongService {
    private final UserSongRepository userSongRepository;
    private final SongRepository songRepository;

    public Set<UserSong> getListensForSongAndCreatedAtGreaterThan(Long songId, Date startDate, Date endDate) {
        return userSongRepository.findBySongIdAndCreatedAtBetween(songId, startDate, endDate);
    }

    public Set<UserSong> getSongsForUsersAndCreatedAtGreaterThan(Collection<Long> ids,
                                                                 Collection<Long> tagIds,
                                                                 Date startDate,
                                                                 Date endDate) {
        return userSongRepository.findByUserIdInAndCreatedAtBetween(ids, tagIds, startDate, endDate);
    }

    public Set<UserSong> getSongsForUser(Long userId, Date start, Date end) {
        return userSongRepository.findByUserIdAndCreatedAtBetween(userId, start, end);
    }

    public List<Long> filterOutRestrictedSongs(Long userId, List<Long> songIds) {
        var restrictedSongs = new HashSet<>(songRepository.findByProtectionTypePublic(songIds, userId));
        return songIds.stream().filter(id -> !restrictedSongs.contains(id)).toList();
    }
}
