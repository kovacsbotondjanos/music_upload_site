package com.musicUpload.dataHandler.services;

import com.musicUpload.dataHandler.DTOs.FilteredUserDTO;
import com.musicUpload.dataHandler.DTOs.UserDTO;
import com.musicUpload.dataHandler.details.UserDetailsImpl;
import com.musicUpload.dataHandler.enums.Privilege;
import com.musicUpload.dataHandler.models.implementations.User;
import com.musicUpload.dataHandler.repositories.UserRepository;
import com.musicUpload.exceptions.*;
import com.musicUpload.util.ImageFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final ImageFactory imageFactory;
    private final MinioService minioService;
    private final String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static UserDetailsImpl getCurrentUserDetails() {
        if (SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetailsImpl userDetails) {
            return userDetails;
        }
        return null;
    }

    public static Long getCurrentUserId() {
        return Optional.ofNullable(getCurrentUserDetails())
                .map(UserDetailsImpl::getId)
                .orElse(null);
    }

    public static UserDetailsImpl getCurrentUserDetailsOrThrowError() {
        return Optional.ofNullable(getCurrentUserDetails())
                .orElseThrow(UnauthenticatedException::new);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(
                        user.getPrivilege()
                ),
                user.getProfilePicture()
        );
    }

    public User registerUser(User user) {

        if (user.getEmail() == null || user.getPassword() == null || user.getUsername() == null) {
            throw new WrongFormatException("Please fill out all the fields");
        }

        if (user.getPassword().length() < 8) {
            throw new NotAcceptableException("Password is in wrong format");
        }

        if (user.getUsername().isEmpty()) {
            throw new NotAcceptableException("Please fill the name field with valid data");
        }

        Pattern p = Pattern.compile(ePattern);
        Matcher m = p.matcher(user.getEmail());
        if (!m.matches()) {
            throw new WrongFormatException("Please fill the email field with valid data");
        }

        if (user.getPrivilege() == null) {
            user.setPrivilege(Privilege.USER);
        }

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new NotAcceptableException("Username already exists");
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new NotAcceptableException("Email already exists");
        }

        user.setProfilePicture(imageFactory.getRandomImage(user.getUsername()));

        user.setPassword(encoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public UserDTO findCurrUser() {
        UserDetailsImpl userDetails = getCurrentUserDetailsOrThrowError();

        return findById(userDetails.getId()).map(u -> UserDTO.of(u, minioService.getImage(u.getProfilePicture())))
                .orElseThrow(NotFoundException::new);
    }

    public List<FilteredUserDTO> findByNameLike(String name, int pageNumber, int pageSize) {
        UserDetailsImpl userDetails = getCurrentUserDetails();
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        return userRepository.findByNameLike(name, Optional.ofNullable(userDetails).map(UserDetailsImpl::getId).orElse(0L), pageable)
                .stream()
                .map(u -> FilteredUserDTO.of(u, minioService.getImage(u.getProfilePicture())))
                .toList();
    }

    public FilteredUserDTO getUserById(Long id) {
        return userRepository.findById(id).map(u -> FilteredUserDTO.of(u, minioService.getImage(u.getProfilePicture()))).orElseThrow();
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public void patchUser(String username,
                          String email,
                          String password,
                          String oldPassword,
                          MultipartFile image) {
        UserDetailsImpl userDetails = getCurrentUserDetailsOrThrowError();

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(NotFoundException::new);

        if (username != null && !username.isEmpty()) {
            if (userRepository.findByUsername(username).isPresent()) {
                throw new NotAcceptableException("Username already exists");
            }
            user.setUsername(username);
        }

        if (email != null) {
            if (userRepository.findByEmail(email).isPresent()) {
                throw new NotAcceptableException("Email already exists");
            }

            Pattern p = Pattern.compile(ePattern);
            Matcher m = p.matcher(user.getEmail());
            if (!m.matches()) {
                throw new WrongFormatException();
            }
            user.setEmail(email);
        }

        if (password != null && oldPassword != null) {
            if (password.length() < 8 || !encoder.matches(oldPassword, userDetails.getPassword())) {
                throw new NotAcceptableException();
            }
            user.setPassword(password);
        }

        if (image != null && !image.isEmpty()) {
            if (!Objects.requireNonNull(image.getContentType()).contains("image")) {
                throw new UnprocessableException();
            }
            minioService.deleteImage(user.getProfilePicture());
            user.setProfilePicture(minioService.uploadImage(image));
        }

        userRepository.save(user);
    }

    public void followUser(Long userId) {
        UserDetailsImpl userDetails = getCurrentUserDetailsOrThrowError();

        if (userId.equals(userDetails.getId())) {
            throw new WrongFormatException();
        }

        User u = userRepository.findById(userDetails.getId())
                .orElseThrow(NotFoundException::new);

        User uFollowed = userRepository.findById(userId)
                .orElseThrow(NotFoundException::new);

        List<User> followed = u.getFollowedUsers();
        List<User> followers = uFollowed.getFollowers();

        if (!followed.contains(uFollowed) && !followers.contains(u)) {
            followed.add(uFollowed);
            followers.add(u);
            userRepository.save(u);
            userRepository.save(uFollowed);
        }
    }

    public void deleteUser() {
        UserDetailsImpl userDetails = getCurrentUserDetailsOrThrowError();

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(NotFoundException::new);
        minioService.deleteImage(user.getProfilePicture());
        userRepository.delete(user);
    }

    public long count() {
        return userRepository.count();
    }
}
