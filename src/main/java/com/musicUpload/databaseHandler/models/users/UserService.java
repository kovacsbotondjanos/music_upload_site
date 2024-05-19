package com.musicUpload.databaseHandler.models.users;

import com.musicUpload.databaseHandler.models.albums.AlbumService;
import com.musicUpload.databaseHandler.models.songs.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SongService songService;
    @Autowired
    private AlbumService albumService;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            var userObj = user.get();
            return new CustomUserDetails(
                    userObj.getId(),
                    userObj.getUsername(),
                    userObj.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority(userObj.getAuthority().getName())),
                    userObj.getProfilePicture(),
                    userObj.getSongs(),
                    userObj.getAlbums()
            );
        } else {
            throw new UsernameNotFoundException(username);
        }
    }

    public User registerUser(User user) {
        if(userRepository.findByUsername(user.getUsername()).isPresent()){
            throw new IllegalArgumentException("Username already exists");
        }
        if(userRepository.findByEmail(user.getEmail()).isPresent()){
            throw new IllegalArgumentException("Email already exists");
        }
        user.setPassword(encoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    public User saveUser(User user){
        return userRepository.save(user);
    }

    public List<User> getUsers(){
        return userRepository.findAll();
    }
}
