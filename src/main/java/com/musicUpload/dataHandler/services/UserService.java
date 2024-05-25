package com.musicUpload.dataHandler.services;

import com.musicUpload.dataHandler.details.CustomUserDetails;
import com.musicUpload.dataHandler.models.User;
import com.musicUpload.dataHandler.repositories.UserRepository;
import com.musicUpload.util.ImageFactory;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SongService songService;
    @Autowired
    private AlbumService albumService;
    @Autowired
    private ImageFactory imageFactory;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";

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

        if(user.getEmail() == null || user.getPassword() == null || user.getUsername() == null){
            throw new IllegalArgumentException("Please fill out all the fields");
        }

        if(user.getPassword().length() < 8){
            throw new IllegalArgumentException("Password is in wrong format");
        }

        if(user.getUsername().isEmpty()){
            throw new IllegalArgumentException("Please fill the email field with valid data");
        }

        Pattern p = Pattern.compile(ePattern);
        Matcher m = p.matcher(user.getEmail());
        if(!m.matches()){
            throw new IllegalArgumentException("Please fill the email field with valid data");
        }

        String image = imageFactory.getRandomImage();
        user.setProfilePicture(image);

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