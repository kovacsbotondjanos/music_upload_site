package com.musicUpload.dataHandler.details;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.musicUpload.dataHandler.models.implementations.Album;
import com.musicUpload.dataHandler.models.implementations.Song;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
public class UserDetailsImpl implements UserDetails {
    private final Long id;
    private final String username;
    @JsonIgnore
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final String profilePicture;
    @JsonIgnore
    private List<Song> songs;
    @JsonIgnore
    private List<Album> albums;
    private boolean accountNonLocked;
    private boolean accountNonExpired;
    private boolean credentialsNonExpired;
    private boolean enabled;

    public UserDetailsImpl(Long id, String username, String password, Collection<? extends GrantedAuthority> authorities,
                           String profilePicture, List<Song> songs, List<Album> albums) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.profilePicture = profilePicture;
        this.songs = songs;
        this.albums = albums;
        //TODO: implement these functionalities in the future
        this.accountNonLocked = true;
        this.accountNonExpired = true;
        this.credentialsNonExpired = true;
        this.enabled = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities.stream().toList();
    }

    public List<Song> addSong(Song song) {
        songs.add(song);
        return songs;
    }

    public List<Album> addAlbum(Album album) {
        albums.add(album);
        return albums;
    }
}
