package com.musicUpload.dataHandler.seeder;

import com.musicUpload.dataHandler.models.Album;
import com.musicUpload.dataHandler.services.AlbumService;
import com.musicUpload.dataHandler.models.Auth;
import com.musicUpload.dataHandler.services.AuthService;
import com.musicUpload.dataHandler.models.ProtectionType;
import com.musicUpload.dataHandler.services.ProtectionTypeService;
import com.musicUpload.dataHandler.models.Song;
import com.musicUpload.dataHandler.services.SongService;
import com.musicUpload.dataHandler.models.User;
import com.musicUpload.dataHandler.services.UserService;
import com.musicUpload.dataHandler.seeder.factories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DatabaseSeeder {
    private final UserService userService;
    private final AuthService authService;
    private final ProtectionTypeService protectionTypeService;
    private final UserFactory userFactory;
    private final SongFactory songFactory;
    private final AlbumFactory albumFactory;
    private final AuthFactory authFactory;
    private final ProtectionTypeFactory protectionTypeFactory;

    @Autowired
    public DatabaseSeeder(UserService userService, AuthService authService, ProtectionTypeService protectionTypeService, UserFactory userFactory, SongFactory songFactory, AlbumFactory albumFactory, AuthFactory authFactory, ProtectionTypeFactory protectionTypeFactory) {
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

        users = userFactory.createFollow(users);

        List<Song> songs = songFactory.generateSongs(40, users, protectionTypes);

        albumFactory.createAlbums(20, users, songs, protectionTypes);
    }

    public void seedDatabaseIfEmpty(){
        if(userService.getUsers().isEmpty()){
            List<Auth> auths = authFactory.createAuthorities();

            protectionTypeFactory.generateProtectionTypes();

            Optional<Auth> auth = auths.stream().filter(a -> a.getName().equals("ADMIN")).findAny();
            auth.ifPresent(userFactory::createAdminFromConfigFile);
            seedDatabase();
        }
    }
}