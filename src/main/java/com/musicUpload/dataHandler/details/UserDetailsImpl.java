package com.musicUpload.dataHandler.details;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
public class UserDetailsImpl implements UserDetails {
    private final Long id;
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final String profilePicture;
    private boolean accountNonLocked;
    private boolean accountNonExpired;
    private boolean credentialsNonExpired;
    private boolean enabled;

    public UserDetailsImpl(Long id,
                           String username,
                           String password,
                           Collection<? extends GrantedAuthority> authorities,
                           String profilePicture) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.profilePicture = profilePicture;
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

}
