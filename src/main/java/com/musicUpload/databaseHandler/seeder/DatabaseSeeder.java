package com.musicUpload.databaseHandler.seeder;

import com.musicUpload.databaseHandler.models.albums.Album;
import com.musicUpload.databaseHandler.models.albums.AlbumService;
import com.musicUpload.databaseHandler.models.songs.Song;
import com.musicUpload.databaseHandler.models.songs.SongService;
import com.musicUpload.databaseHandler.models.users.User;
import com.musicUpload.databaseHandler.models.users.UserService;
import com.musicUpload.databaseHandler.seeder.factories.AlbumFactory;
import com.musicUpload.databaseHandler.seeder.factories.SongFactory;
import com.musicUpload.databaseHandler.seeder.factories.UserFactory;
import com.musicUpload.util.ImageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DatabaseSeeder {
    @Autowired
    private final UserService userService;
    @Autowired
    private final UserFactory userFactory;
    @Autowired
    private final SongFactory songFactory;
    @Autowired
    private final AlbumFactory albumFactory;

    public DatabaseSeeder(UserService userService, UserFactory userFactory, SongFactory songFactory, AlbumFactory albumFactory) {
        this.userService = userService;
        this.userFactory = userFactory;
        this.songFactory = songFactory;
        this.albumFactory = albumFactory;
    }

    public void seedDatabaseIfEmpty(){
        List<User> users = userFactory.createUsers(10);
        List<Song> songs = songFactory.generateSongs(10, users);
        List<Album> albums = albumFactory.createAlbums(10, users, songs);

        users.stream().parallel().forEach(userService::registerUser);
    }
}
