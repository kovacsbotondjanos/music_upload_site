package com.musicUpload.musicUpload.recommendationEngine.database.service;

import com.musicUpload.musicUpload.recommendationEngine.database.entity.UserSong;
import com.musicUpload.musicUpload.recommendationEngine.database.repository.UserSongRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

@Service
public class UserSongService {
    private final UserSongRepository userSongRepository;

    public UserSongService(UserSongRepository userSongRepository) {
        this.userSongRepository = userSongRepository;
    }

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
}
