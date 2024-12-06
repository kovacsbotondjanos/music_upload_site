package com.musicUpload.dataHandler.seeder.factories;

import com.github.javafaker.Faker;
import com.musicUpload.dataHandler.enums.Privilege;
import com.musicUpload.dataHandler.models.implementations.User;
import com.musicUpload.dataHandler.services.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.stream.IntStream;

@Service
public class UserFactory {
    private final UserService userService;

    @Value("${admin.username}")
    private String adminUserName;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.email}")
    private String adminEmail;

    @Autowired
    public UserFactory(UserService userService) {
        this.userService = userService;
    }

    public List<User> createFollow(List<User> users, ExecutorService executorService) {
        Random random = new Random();

        users.stream().parallel().forEach(currUser -> {
            IntStream.range(0, random.nextInt() % users.size()).forEachOrdered(__ ->
                executorService.submit(() -> {
                    User user = users.get(random.nextInt(0, Integer.MAX_VALUE) % users.size());
                    if (currUser.getFollowedUsers().stream().noneMatch(u -> u.equals(user) || u.equals(currUser))) {
                        currUser.getFollowedUsers().add(user);
                    }
                })
            );
            userService.saveUser(currUser);
        });

        return users;
    }

    public List<User> createUsers(int number, ExecutorService executorService) {
        List<User> users = Collections.synchronizedList(new ArrayList<>());

        IntStream.range(0, number).parallel().forEachOrdered(__ ->
            executorService.submit(() -> {
                User user = createUser();
                users.add(user);
                userService.registerUser(user);
            })
        );

        return users;
    }

    public void createAdminFromConfigFile() {
        User admin = new User();
        admin.setUsername(adminUserName);
        admin.setPassword(adminPassword);
        admin.setEmail(adminEmail);
        admin.setPrivilege(Privilege.ADMIN);
        userService.registerUser(admin);
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
