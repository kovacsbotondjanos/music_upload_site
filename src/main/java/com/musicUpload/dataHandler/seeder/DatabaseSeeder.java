package com.musicUpload.dataHandler.seeder;

import com.musicUpload.dataHandler.models.implementations.Song;
import com.musicUpload.dataHandler.models.implementations.User;
import com.musicUpload.dataHandler.seeder.factories.AlbumFactory;
import com.musicUpload.dataHandler.seeder.factories.SongFactory;
import com.musicUpload.dataHandler.seeder.factories.UserFactory;
import com.musicUpload.dataHandler.services.UserService;
import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DatabaseSeeder {
    private static final Logger logger = LogManager.getLogger(DatabaseSeeder.class);
    private final UserService userService;
    private final UserFactory userFactory;
    private final SongFactory songFactory;
    private final AlbumFactory albumFactory;

    @Autowired
    public DatabaseSeeder(UserService userService,
                          UserFactory userFactory,
                          SongFactory songFactory,
                          AlbumFactory albumFactory) {
        this.userService = userService;
        this.userFactory = userFactory;
        this.songFactory = songFactory;
        this.albumFactory = albumFactory;
    }

    public void seedDatabase() {
        List<User> users = userFactory.createUsers(10);
        users = userFactory.createFollow(users);
        List<Song> songs = songFactory.generateSongs(40, users);
        albumFactory.createAlbums(20, users, songs);
    }

    @PostConstruct
    public void seedDatabaseIfEmpty() {
        if (userService.count() == 0) {
            userFactory.createAdminFromConfigFile();
            seedDatabase();
            logger.info("Database seeding completed");
        }
    }
}
