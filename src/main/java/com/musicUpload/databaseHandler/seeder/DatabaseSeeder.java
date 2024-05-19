package com.musicUpload.databaseHandler.seeder;

import com.musicUpload.databaseHandler.models.albums.Album;
import com.musicUpload.databaseHandler.models.albums.AlbumService;
import com.musicUpload.databaseHandler.models.auth.Auth;
import com.musicUpload.databaseHandler.models.auth.AuthService;
import com.musicUpload.databaseHandler.models.protectionType.ProtectionType;
import com.musicUpload.databaseHandler.models.protectionType.ProtectionTypeService;
import com.musicUpload.databaseHandler.models.songs.Song;
import com.musicUpload.databaseHandler.models.songs.SongService;
import com.musicUpload.databaseHandler.models.users.User;
import com.musicUpload.databaseHandler.models.users.UserService;
import com.musicUpload.databaseHandler.seeder.factories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DatabaseSeeder {
    @Autowired
    private final SongService songService;
    @Autowired
    private final AlbumService albumService;
    @Autowired
    private final UserService userService;
    @Autowired
    private final AuthService authService;
    @Autowired
    private final ProtectionTypeService protectionTypeService;
    @Autowired
    private final UserFactory userFactory;
    @Autowired
    private final SongFactory songFactory;
    @Autowired
    private final AlbumFactory albumFactory;
    @Autowired
    private final AuthFactory authFactory;
    @Autowired
    private final ProtectionTypeFactory protectionTypeFactory;

    public DatabaseSeeder(SongService songService, AlbumService albumService, UserService userService, AuthService authService, ProtectionTypeService protectionTypeService, UserFactory userFactory, SongFactory songFactory, AlbumFactory albumFactory, AuthFactory authFactory, ProtectionTypeFactory protectionTypeFactory) {
        this.songService = songService;
        this.albumService = albumService;
        this.userService = userService;
        this.authService = authService;
        this.protectionTypeService = protectionTypeService;
        this.userFactory = userFactory;
        this.songFactory = songFactory;
        this.albumFactory = albumFactory;
        this.authFactory = authFactory;
        this.protectionTypeFactory = protectionTypeFactory;
    }

    public void seedDatabaseIfEmpty(){
        List<Auth> auths = authFactory.createAuthorities();
        auths.stream().parallel().forEach(authService::save);

        List<ProtectionType> protectionTypes = protectionTypeFactory.generateProtectionTypes();
        protectionTypes.stream().parallel().forEach(protectionTypeService::save);

        List<User> users = userFactory.createUsers(10, auths);
        users.stream().parallel().forEach(userService::registerUser);

        users = userFactory.createFollow(users);
        users.stream().parallel().forEach(userService::saveUser);

        List<Song> songs = songFactory.generateSongs(10, users, protectionTypes);
        songs.stream().parallel().forEach(songService::saveSong);

        List<Album> albums = albumFactory.createAlbums(10, users, songs, protectionTypes);
        albums.stream().parallel().forEach(albumService::saveAlbum);
    }
}
