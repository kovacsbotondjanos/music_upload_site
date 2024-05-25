package com.musicUpload.dataHandler.seeder.factories;

import com.github.javafaker.Faker;
import com.musicUpload.dataHandler.models.Album;
import com.musicUpload.dataHandler.models.ProtectionType;
import com.musicUpload.dataHandler.models.Song;
import com.musicUpload.dataHandler.models.User;
import com.musicUpload.dataHandler.services.AlbumService;
import com.musicUpload.util.ImageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

@Service
public class AlbumFactory {
    private final ImageFactory imageFactory;
    private final AlbumService albumService;

    @Autowired
    public AlbumFactory(ImageFactory imageFactory, AlbumService albumService) {
        this.imageFactory = imageFactory;
        this.albumService = albumService;
    }

    public List<Album> createAlbums(int number, List<User> users, List<Song> songs, List<ProtectionType> protectionTypes){
        List<Album> albums = new CopyOnWriteArrayList<>();

        IntStream.range(0, number).parallel().forEachOrdered(__ -> {
            Album album = createAlbum(users, songs, protectionTypes);
            albumService.saveAlbum(album);
            albums.add(album);
        });

        return albums;
    }

    private Album createAlbum(List<User> users, List<Song> songs, List<ProtectionType> protectionTypes){
        Album album = new Album();
        Random random = new Random();

        Faker faker = new Faker(new Random());
        String name = faker.book().title();

        album.setName(name);
        album.setImage(imageFactory.getRandomImage());
        album.setProtectionType(protectionTypes.get(random.nextInt(1, Integer.MAX_VALUE) % protectionTypes.size()));

        User user = users.get(random.nextInt(1, Integer.MAX_VALUE) % users.size());
        synchronized (user){
            album.setUser(user);
            user.getAlbums().add(album);
        }

        IntStream.range(0, random.nextInt(1, Integer.MAX_VALUE) % songs.size()).forEachOrdered(__ -> {
            Song song = songs.get(random.nextInt(1, Integer.MAX_VALUE) % songs.size());
            synchronized (song){
                if(album.getSongs().stream().noneMatch(s -> s.equals(song))){
                    album.getSongs().add(song);
                    song.getAlbums().add(album);
                }
            }
        });

        return album;
    }
}
