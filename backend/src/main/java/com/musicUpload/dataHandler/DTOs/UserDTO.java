package com.musicUpload.dataHandler.DTOs;

import com.musicUpload.dataHandler.models.implementations.Album;
import com.musicUpload.dataHandler.models.implementations.Song;
import com.musicUpload.dataHandler.models.implementations.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String profilePicture;
    private String email;
    private String username;
    private List<Long> albums;
    private List<Long> songs;
    private String authority;
    private List<Long> followedUsers;
    private List<Long> followers;
    private Date createdAt;

    public UserDTO(User user, String image) {
        this.id = user.getId();
        this.profilePicture = image;
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.albums = user.getAlbums().stream().map(Album::getId).toList();
        this.songs = user.getSongs().stream().map(Song::getId).toList();
        this.authority = user.getPrivilege().name();
        this.followers = user.getFollowers().stream().map(User::getId).toList();
        this.followedUsers = user.getFollowedUsers().stream().map(User::getId).toList();
        this.createdAt = user.getCreatedAt();
    }

    public static UserDTO of(User user, String image) {
        return new UserDTO(user, image);
    }
}
