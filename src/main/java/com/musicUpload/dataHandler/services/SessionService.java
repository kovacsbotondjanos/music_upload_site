package com.musicUpload.dataHandler.services;

import com.musicUpload.dataHandler.details.UserDetailsImpl;
import com.musicUpload.dataHandler.models.implementations.Album;
import com.musicUpload.dataHandler.models.implementations.Song;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Service;

@Service
public class SessionService {
    @Autowired
    private final HttpSession session;

    @Value("spring.session.context-name")
    private String attributeName;

    public SessionService(HttpSession httpSession) {
        this.session = httpSession;
    }

    private SecurityContextImpl getSecurityContext() {
        return (SecurityContextImpl) session.getAttribute(attributeName);
    }

    private void updateUserDetails(SecurityContextImpl securityContext) {
        session.setAttribute(attributeName, securityContext);
    }

    public void addSong(Song song) {
        SecurityContextImpl securityContext = getSecurityContext();
        UserDetailsImpl userDetails = (UserDetailsImpl) securityContext
                                    .getAuthentication().getPrincipal();
        if (userDetails != null) {
            userDetails.addSong(song);
            updateUserDetails(securityContext);
        }
    }

    public void addAlbum(Album album) {
        SecurityContextImpl securityContext = getSecurityContext();
        UserDetailsImpl userDetails = (UserDetailsImpl) securityContext
                .getAuthentication().getPrincipal();
        if (userDetails != null) {
            userDetails.addAlbum(album);
            updateUserDetails(securityContext);
        }
    }

    public void deleteSong(Song song) {
        SecurityContextImpl securityContext = getSecurityContext();
        UserDetailsImpl userDetails = (UserDetailsImpl) securityContext
                .getAuthentication().getPrincipal();
        if (userDetails != null) {
            userDetails.removeSong(song);
            updateUserDetails(securityContext);
        }
    }

    public void deleteAlbum(Album aLbum) {
        SecurityContextImpl securityContext = getSecurityContext();
        UserDetailsImpl userDetails = (UserDetailsImpl) securityContext
                .getAuthentication().getPrincipal();
        if (userDetails != null) {
            userDetails.removeAlbum(aLbum);
            updateUserDetails(securityContext);
        }
    }
}
