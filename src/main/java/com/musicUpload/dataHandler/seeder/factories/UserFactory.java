package com.musicUpload.dataHandler.seeder.factories;

import com.github.javafaker.Faker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.musicUpload.dataHandler.enums.Privilege;
import com.musicUpload.dataHandler.models.implementations.User;
import com.musicUpload.dataHandler.services.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@Service
public class UserFactory {
    private static final Logger logger = LogManager.getLogger(UserFactory.class);
    private final UserService userService;

    @Autowired
    public UserFactory(UserService userService) {
        this.userService = userService;
    }

    public List<User> createFollow(List<User> users) {
        Random random = new Random();

        users.stream().parallel().forEach(currUser -> {
            IntStream.range(0, random.nextInt() % users.size()).forEachOrdered(__ -> {
                User user = users.get(random.nextInt(0, Integer.MAX_VALUE) % users.size());
                if (currUser.getFollowedUsers().stream().noneMatch(u -> u.equals(user) || u.equals(currUser))) {
                    currUser.getFollowedUsers().add(user);
                }
            });
            userService.saveUser(currUser);
        });

        return users;
    }

    public List<User> createUsers(int number) {
        List<User> users = Collections.synchronizedList(new ArrayList<>());

        IntStream.range(0, number).parallel().forEachOrdered(__ -> {
            User user = createUser();
            users.add(user);
            userService.registerUser(user);
        });

        return users;
    }

    public void createAdminFromConfigFile() {
        User admin;
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream("adminConfig.json")) {
            assert inputStream != null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                admin = gson.fromJson(reader, User.class);
                admin.setPrivilege(Privilege.ADMIN);
                userService.registerUser(admin);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private User createUser() {
        Faker faker = new Faker(new Random());
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String email = faker.internet().safeEmailAddress();

        User user = new User();
        user.setUsername(firstName + " " + lastName);
        user.setEmail(email);
        user.setPrivilege(Privilege.USER);

        user.setPassword("password");

        return user;
    }
}
