package com.musicUpload.databaseHandler.seeder;

import com.musicUpload.databaseHandler.models.Album;
import com.musicUpload.databaseHandler.services.AlbumService;
import com.musicUpload.databaseHandler.models.Auth;
import com.musicUpload.databaseHandler.services.AuthService;
import com.musicUpload.databaseHandler.models.ProtectionType;
import com.musicUpload.databaseHandler.services.ProtectionTypeService;
import com.musicUpload.databaseHandler.models.Song;
import com.musicUpload.databaseHandler.services.SongService;
import com.musicUpload.databaseHandler.models.User;
import com.musicUpload.databaseHandler.services.UserService;
import com.musicUpload.databaseHandler.seeder.factories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public void seedDatabase(){
        List<Auth> auths = authService.getAllPossibleAuth();
        List<ProtectionType> protectionTypes = protectionTypeService.getAllPossibleProtectionType();

        List<User> users = userFactory.createUsers(10, auths);
        users.stream().parallel().forEach(userService::registerUser);

        users = userFactory.createFollow(users);
        users.stream().parallel().forEach(userService::saveUser);

        List<Song> songs = songFactory.generateSongs(50, users, protectionTypes);
        songs.stream().parallel().forEach(songService::saveSong);

        List<Album> albums = albumFactory.createAlbums(20, users, songs, protectionTypes);
        albums.stream().parallel().forEach(albumService::saveAlbum);
    }

    public void seedDatabaseIfEmpty(){
        if(userService.getUsers().isEmpty()){
            List<Auth> auths = authFactory.createAuthorities();
            auths.stream().parallel().forEach(authService::save);

            List<ProtectionType> protectionTypes = protectionTypeFactory.generateProtectionTypes();
            protectionTypes.stream().parallel().forEach(protectionTypeService::save);

            Optional<User> user = userFactory.createAdminFromConfigFile();
            user.ifPresent(value -> {
                Optional<Auth> auth = auths.stream().filter(a -> a.getName().equals("ADMIN")).findAny();
                auth.ifPresent(value::setAuthority);
                userService.registerUser(user.get());
            });
            seedDatabase();
        }
    }
}
