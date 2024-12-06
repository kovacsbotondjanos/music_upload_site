package com.musicUpload.dataHandler.services;

import com.musicUpload.dataHandler.models.implementations.UserSong;
import com.musicUpload.dataHandler.repositories.UserSongRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class UserSongService {
    private final UserSongRepository userSongRepository;

    public UserSongService(UserSongRepository userSongRepository) {
        this.userSongRepository = userSongRepository;
    }

    public List<UserSong> getUserSongs() {
        return userSongRepository.findAll();
    }

    public Set<UserSong> getListensForSong(Long songId) {
        return userSongRepository.findBySongId(songId);
    }

    public Set<UserSong> getSongsForUsers(Collection<Long> ids) {
        return userSongRepository.findByUserIdIn(ids);
    }

    public Set<UserSong> getSongsForUser(Long userId) {
        return userSongRepository.findByUserId(userId);
    }

    public Set<UserSong> findByLastTwoMonths(Date start) {
        return userSongRepository.findByCreatedAtGreaterThan(start);
    }
}
