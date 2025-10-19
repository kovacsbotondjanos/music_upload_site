package com.musicUpload.dataHandler.DTOs;

import com.musicUpload.dataHandler.enums.ProtectionType;
import com.musicUpload.dataHandler.models.implementations.Album;
import com.musicUpload.dataHandler.models.implementations.Song;
import com.musicUpload.dataHandler.models.implementations.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FilteredUserDTO {
    private Long id;
    private String profilePicture;
    private String username;
    private List<Long> albums;
    private List<Long> songs;
    private List<Long> followedUsers;
    private List<Long> followers;

    public FilteredUserDTO(User user, String profilePicture) {
        this(
                user.getId(),
                profilePicture,
                user.getUsername(),
                user.getAlbums()
                        .stream()
                        .filter(
                                album -> album.getProtectionType() == ProtectionType.PUBLIC
                        ).map(Album::getId)
                        .toList(),
                user.getSongs()
                        .stream()
                        .filter(
                                album -> album.getProtectionType() == ProtectionType.PUBLIC
                        ).map(Song::getId)
                        .toList(),
                user.getFollowedUsers().stream().map(User::getId).toList(),
                user.getFollowers().stream().map(User::getId).toList()
        );
    }

    public static FilteredUserDTO of(User user, String profilePicture) {
        return new FilteredUserDTO(user, profilePicture);
    }
}
