package com.musicUpload.dataHandler.seeder;

import com.musicUpload.dataHandler.models.implementations.Song;
import com.musicUpload.dataHandler.models.implementations.Tag;
import com.musicUpload.dataHandler.models.implementations.User;
import com.musicUpload.dataHandler.seeder.factories.AlbumFactory;
import com.musicUpload.dataHandler.seeder.factories.SongFactory;
import com.musicUpload.dataHandler.seeder.factories.TagFactory;
import com.musicUpload.dataHandler.seeder.factories.UserFactory;
import com.musicUpload.dataHandler.services.UserService;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class DatabaseSeeder {
    private final UserService userService;
    private final UserFactory userFactory;
    private final SongFactory songFactory;
    private final AlbumFactory albumFactory;
    private final TagFactory tagFactory;

    @Autowired
    public DatabaseSeeder(UserService userService,
                          UserFactory userFactory,
                          SongFactory songFactory,
                          AlbumFactory albumFactory,
                          TagFactory tagFactory) {
        this.userService = userService;
        this.userFactory = userFactory;
        this.songFactory = songFactory;
        this.albumFactory = albumFactory;
        this.tagFactory = tagFactory;
    }

    private void seedDatabase(ExecutorService executorService, User admin) {
        List<User> users = userFactory.createUsers(10, executorService);
        users = userFactory.createFollow(users, executorService);
        users.add(admin);
        List<Tag> tags = tagFactory.initTags(10);
        List<Song> songs = songFactory.generateSongs(40, users, tags);
        albumFactory.createAlbums(20, users, songs);
    }

    @Scheduled(initialDelay = 1000)
    @SchedulerLock(name = "DatabaseSeeder_seedDatabaseIfEmpty", lockAtMostFor = "5m")
    public void seedDatabaseIfEmpty() {
        if (userService.count() == 0) {
            try (ExecutorService executorService = Executors.newFixedThreadPool(20)) {
                User admin = userFactory.createAdminFromConfigFile();
                seedDatabase(executorService, admin);
                log.info("Database seeding completed");
            }
        }
    }
}
