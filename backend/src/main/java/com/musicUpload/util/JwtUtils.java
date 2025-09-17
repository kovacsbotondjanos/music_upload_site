package com.musicUpload.util;

import com.musicUpload.dataHandler.details.UserDetailsImpl;
import com.musicUpload.dataHandler.enums.Privilege;
import com.musicUpload.dataHandler.models.implementations.User;
import com.musicUpload.dataHandler.repositories.UserRepository;
import com.musicUpload.exceptions.UnauthenticatedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtils {

    private static final Key key = Keys.hmacShaKeyFor("ab67f80b4d465e75c08310f9740596c7539ce40a3664f8bee1136c1fc8104c39".getBytes(StandardCharsets.UTF_8));
    private static final long EXPIRATION_MS = 86400000;
    private static final String ID = "id";
    private static final String USERNAME = "username";
    private static final String AUTHORITIES = "authorities";
    private static final String PROFILE_PICTURE = "profilePicture";
    private final UserRepository userRepository;

    public String generateToken(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(UnauthenticatedException::new);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .claim(ID, user.getId())
                .claim(USERNAME, user.getUsername())
                .claim(AUTHORITIES, user.getPrivilege().getAuthority())
                .claim(PROFILE_PICTURE, user.getProfilePicture())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(key)
                .compact();
    }

    public String generateToken(Authentication authentication) {
        return generateToken(authentication.getName());
    }

    public UserDetailsImpl validateTokenAndGetUsername(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return UserDetailsImpl.builder()
                .id(claims.get(ID, Long.class))
                .username(claims.get(USERNAME, String.class))
                .authorities(List.of(Privilege.getByName(claims.get(AUTHORITIES, String.class))))
                .profilePicture(claims.get(PROFILE_PICTURE, String.class))
                .build();
    }
}

