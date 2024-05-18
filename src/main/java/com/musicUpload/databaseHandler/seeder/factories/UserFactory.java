package com.musicUpload.databaseHandler.seeder.factories;

import com.github.javafaker.Faker;
import com.musicUpload.databaseHandler.models.songs.Song;
import com.musicUpload.databaseHandler.models.users.User;
import com.musicUpload.util.ImageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

@Service
public class UserFactory {
    @Autowired
    private final ImageFactory imageFactory;

    public UserFactory(ImageFactory imageFactory) {
        this.imageFactory = imageFactory;
        imageFactory.createImagesDir();
    }

    public List<User> createUsers(int number){
        List<User> users = new CopyOnWriteArrayList<>();

        IntStream.range(0, number).parallel().forEachOrdered(__ -> users.add(createUser()));

        Random random = new Random();

        users.stream().parallel().forEach(currUser -> {
            User user = users.get(random.nextInt(1, Integer.MAX_VALUE) % users.size());
            if(user.getFollowedUsers().stream().noneMatch(u -> u.equals(user) || u.equals(currUser))){
                currUser.getFollowedUsers().add(user);
            }
        });

        return users;
    }

    private User createUser(){
        Faker faker = new Faker(new Random());
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String email = faker.internet().safeEmailAddress();

        User user = new User();
        user.setUsername(firstName + " " + lastName);
        user.setEmail(email);
        user.setRole("USER");
        user.setPassword("password");
        user.setProfilePicture(imageFactory.getRandomImage());

        return user;
    }
}
