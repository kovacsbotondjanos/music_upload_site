package com.musicUpload.dataHandler.seeder.factories;

import com.github.javafaker.Faker;
import com.musicUpload.dataHandler.enums.ProtectionType;
import com.musicUpload.dataHandler.models.implementations.Album;
import com.musicUpload.dataHandler.models.implementations.Song;
import com.musicUpload.dataHandler.models.implementations.User;
import com.musicUpload.dataHandler.repositories.AlbumRepository;
import com.musicUpload.util.ImageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@Service
public class AlbumFactory {
    private final ImageFactory imageFactory;
    private final AlbumRepository albumRepository;

    @Autowired
    public AlbumFactory(ImageFactory imageFactory, AlbumRepository albumRepository) {
        this.imageFactory = imageFactory;
        this.albumRepository = albumRepository;
    }

    public List<Album> createAlbums(int number, List<User> users,
                                    List<Song> songs) {

        return albumRepository.saveAll(
                IntStream.range(0, number)
                        .mapToObj(__ -> createAlbum(users, songs))
                        .toList()
        );
    }

    private Album createAlbum(List<User> users, List<Song> songs) {
        Album album = new Album();
        Random random = new Random();

        Faker faker = new Faker(new Random());
        String name = faker.book().title();

        album.setName(name);
        album.setImage(imageFactory.getRandomImage());
        album.setProtectionType(ProtectionType.getRandomPrivilege());

        User user = users.get(random.nextInt(0, Integer.MAX_VALUE) % users.size());
        synchronized (user) {
            album.setUser(user);
            user.getAlbums().add(album);
        }

        IntStream.range(0, random.nextInt(0, Integer.MAX_VALUE) % songs.size()).forEachOrdered(__ -> {
            Song song = songs.get(random.nextInt(0, Integer.MAX_VALUE) % songs.size());
            synchronized (song) {
                if (album.getSongs().stream().noneMatch(s -> s.equals(song))) {
                    album.getSongs().add(song);
                    song.getAlbums().add(album);
                }
            }
        });

        return album;
    }
}
