package com.musicUpload.dataHandler.DTOs;

import com.musicUpload.dataHandler.details.CustomUserDetails;
import com.musicUpload.dataHandler.models.Album;
import com.musicUpload.dataHandler.models.Song;
import com.musicUpload.dataHandler.models.User;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
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

    public UserDTO(User user){
        this.id = user.getId();
        this.profilePicture = user.getProfilePicture();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.albums = user.getAlbums().stream().map(Album::getId).toList();
        this.songs = user.getSongs().stream().map(Song::getId).toList();
        this.authority = user.getAuthority().getName();
        this.followers = user.getFollowers().stream().map(User::getId).toList();
        this.followedUsers = user.getFollowedUsers().stream().map(User::getId).toList();
        this.createdAt = user.getCreatedAt();
    }
}
