package com.musicUpload.dataHandler.services;

import com.musicUpload.dataHandler.DTOs.UserDTO;
import com.musicUpload.dataHandler.details.CustomUserDetails;
import com.musicUpload.dataHandler.models.Auth;
import com.musicUpload.dataHandler.models.User;
import com.musicUpload.dataHandler.repositories.UserRepository;
import com.musicUpload.exceptions.*;
import com.musicUpload.util.ImageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final AuthService authService;
    private final ImageFactory imageFactory;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";

    @Autowired
    public UserService(UserRepository userRepository, AuthService authService, ImageFactory imageFactory) {
        this.userRepository = userRepository;
        this.authService = authService;
        this.imageFactory = imageFactory;
    }

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

    public void patchUser(CustomUserDetails userDetails,
                          String username,
                          String email,
                          String password,
                          String oldPassword,
                          MultipartFile image) {
        //TODO: return something if these details doesnt match
        if(userDetails == null){
            throw new UnauthenticatedException();
        }

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(UserNotFoundException::new);

        if(username != null && !username.isEmpty()){
            user.setUsername(username);
        }

        if(email != null){
            Pattern p = Pattern.compile(ePattern);
            Matcher m = p.matcher(user.getEmail());
            if(!m.matches()){
                throw new EmailInWrongFormatException();
            }
            user.setEmail(email);
        }

        if(password != null && oldPassword != null){
            if(password.length() < 8 || !encoder.matches(oldPassword, userDetails.getPassword())) {
                throw new PasswordInWrongFormatException();
            }
            user.setPassword(password);
        }

        if(image != null && !image.isEmpty()){
            if(!Objects.requireNonNull(image.getContentType()).contains("image")){
                throw  new FileIsInWrongFormatException();
            }

            try {
                imageFactory.deleteFile(user.getProfilePicture());
                String hashedFileName = UUID.randomUUID() + ".jpg";
                image.transferTo(new File(imageFactory.getDirName() + "//" + hashedFileName));
                user.setProfilePicture(hashedFileName);
            }
            catch (IOException e){
                System.err.println(e.getMessage());
            }
        }

        User u = userRepository.save(user);
    }

    public User registerUser(User user) {

        if(user.getEmail() == null || user.getPassword() == null || user.getUsername() == null){
            throw new EmptyFieldException("Please fill out all the fields");
        }

        if(user.getPassword().length() < 8){
            throw new PasswordInWrongFormatException("Password is in wrong format");
        }

        if(user.getUsername().isEmpty()){
            throw new NameInWrongFormatException("Please fill the name field with valid data");
        }

        Pattern p = Pattern.compile(ePattern);
        Matcher m = p.matcher(user.getEmail());
        if(!m.matches()){
            throw new EmailInWrongFormatException("Please fill the email field with valid data");
        }


        //TODO: This will have to change in the future, bc it can be unsafe
        if(user.getAuthority() == null){
            Auth auth = authService.getByName("USER")
                            .orElseThrow(IllegalArgumentException::new);
            user.setAuthority(auth);
        }

        if(userRepository.findByUsername(user.getUsername()).isPresent()){
            throw new NameInWrongFormatException("Username already exists");
        }

        if(userRepository.findByEmail(user.getEmail()).isPresent()){
            throw new EmailInWrongFormatException("Email already exists");
        }

        String image = imageFactory.getRandomImage();
        user.setProfilePicture(image);

        user.setPassword(encoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    public User saveUser(User user){
        return userRepository.save(user);
    }

    public List<User> getUsers(){
        return userRepository.findAll();
    }

    public UserDTO findCurrUser(CustomUserDetails userDetails){
        if(userDetails == null){
            throw new UnauthenticatedException();
        }

        return findById(userDetails.getId()).map(UserDTO::new)
                .orElseThrow(UserNotFoundException::new);
    }

    public Optional<User> findById(Long id){
        return userRepository.findById(id);
    }
}
