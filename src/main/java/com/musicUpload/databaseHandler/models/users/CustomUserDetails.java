package com.musicUpload.databaseHandler.models.users;

import com.musicUpload.databaseHandler.models.albums.Album;
import com.musicUpload.databaseHandler.models.songs.Song;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
public class CustomUserDetails implements UserDetails {
    private final Long id;
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final String profilePicture;
    private List<Song> songs;
    private List<Album> albums;
    private boolean accountNonLocked;
    private boolean accountNonExpired;
    private boolean credentialsNonExpired;
    private boolean enabled;

    public CustomUserDetails(Long id, String username, String password, Collection<? extends GrantedAuthority> authorities,
                             String profilePicture, List<Song> songs, List<Album> albums) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.profilePicture = profilePicture;
        this.songs = songs;
        this.albums = albums;
        //TODO: create these functionalities in the future
        this.accountNonLocked = true;
        this.accountNonExpired = true;
        this.credentialsNonExpired = true;
        this.enabled = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities.stream().toList();
    }

    public List<Song> addSong(Song song){
        songs.add(song);
        return songs;
    }
}
