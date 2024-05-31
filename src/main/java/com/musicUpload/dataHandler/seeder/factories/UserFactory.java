package com.musicUpload.dataHandler.seeder.factories;

import com.github.javafaker.Faker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.musicUpload.dataHandler.models.Auth;
import com.musicUpload.dataHandler.models.User;
import com.musicUpload.dataHandler.services.UserService;
import com.musicUpload.util.ImageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

@Service
public class UserFactory {
    private final UserService userService;

    @Autowired
    public UserFactory(ImageFactory imageFactory, UserService userService) {
        this.userService = userService;
        imageFactory.createImagesDir();
    }

    public List<User> createFollow(List<User> users){
        Random random = new Random();

        users.stream().parallel().forEach(currUser -> {
            IntStream.range(0, random.nextInt() % users.size()).forEachOrdered(__ -> {
                User user = users.get(random.nextInt(1, Integer.MAX_VALUE) % users.size());
                if(currUser.getFollowedUsers().stream().noneMatch(u -> u.equals(user) || u.equals(currUser))){
                    currUser.getFollowedUsers().add(user);
                }
            });
            userService.saveUser(currUser);
        });

        return users;
    }

    public List<User> createUsers(int number, List<Auth> auths){
        List<User> users = new CopyOnWriteArrayList<>();

        IntStream.range(0, number).parallel().forEachOrdered(__ -> {
            User user = createUser(auths);
            users.add(user);
            userService.registerUser(user);
        });

        return users;
    }

    public void createAdminFromConfigFile(Auth adminAuth){
        User admin;
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream("adminConfig.json")) {
            assert inputStream != null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                admin = gson.fromJson(reader, User.class);
                admin.setAuthority(adminAuth);
                userService.registerUser(admin);
            }
        }
        catch (Exception e){
            System.err.println(e.getMessage());
        }
    }

    private User createUser(List<Auth> auths){
        Faker faker = new Faker(new Random());
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String email = faker.internet().safeEmailAddress();

        User user = new User();
        user.setUsername(firstName + " " + lastName);
        user.setEmail(email);

        Optional<Auth> auth = auths.stream().filter(a -> a.getName().equals("USER")).findAny();
        auth.ifPresent(user::setAuthority);

        user.setPassword("password");

        return user;
    }
}
