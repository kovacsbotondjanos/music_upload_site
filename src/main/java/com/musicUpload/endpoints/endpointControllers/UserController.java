package com.musicUpload.endpoints.endpointControllers;

import com.musicUpload.databaseHandler.models.users.User;
import com.musicUpload.databaseHandler.models.users.UserService;
import com.musicUpload.util.ImageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private final ImageFactory imageFactory;
    @Autowired
    private final UserService userService;
    private final String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";

    public UserController(ImageFactory imageFactory, UserService userService) {
        this.imageFactory = imageFactory;
        this.userService = userService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> createUser(@ModelAttribute User user){

        if(user.getEmail() == null || user.getPassword() == null || user.getUsername() == null){
            return new ResponseEntity<>("Unable to create user, please provide data for all fields", HttpStatus.NOT_ACCEPTABLE);
        }

        if(user.getPassword().length() <= 8){
            return new ResponseEntity<>("Unable to create user, please provide a password with 8 or more characters", HttpStatus.NOT_ACCEPTABLE);
        }

        if(user.getUsername().isEmpty()){
            return new ResponseEntity<>("Unable to create user, please provide a valid username", HttpStatus.NOT_ACCEPTABLE);
        }

        Pattern p = Pattern.compile(ePattern);
        Matcher m = p.matcher(user.getEmail());
        if(!m.matches()){
            return new ResponseEntity<>("Unable to create user, please provide a valid email", HttpStatus.NOT_ACCEPTABLE);
        }

        String image = imageFactory.getRandomImage();
        user.setProfilePicture(image);

        try{
            userService.registerUser(user);
        }
        catch (IllegalArgumentException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        }

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }
}
