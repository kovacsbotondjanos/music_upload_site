package com.musicUpload.databaseHandler.seeder.factories;

import com.github.javafaker.Faker;
import com.musicUpload.databaseHandler.models.protectionType.ProtectionType;
import com.musicUpload.databaseHandler.models.songs.Song;
import com.musicUpload.databaseHandler.models.users.User;
import com.musicUpload.util.ImageFactory;
import com.musicUpload.util.MusicFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

@Service
public class SongFactory {
    @Autowired
    private final ImageFactory imageFactory;
    @Autowired
    private final MusicFactory musicFactory;

    public SongFactory(ImageFactory imageFactory, MusicFactory musicFactory) {
        this.imageFactory = imageFactory;
        this.musicFactory = musicFactory;
        musicFactory.createMusicDir();
    }

    public List<Song> generateSongs(int number, List<User> users, List<ProtectionType> protectionTypes){
        List<Song> songs = new CopyOnWriteArrayList<>();

        IntStream.range(0, number).parallel().forEachOrdered(__ -> songs.add(createSong(users, protectionTypes)));

        return songs;
    }

    private Song createSong(List<User> users, List<ProtectionType> protectionTypes){
        Song song = new Song();
        Random random = new Random();

        Faker faker = new Faker(random);
        String name = faker.book().title();

        song.setName(name);
        song.setNameHashed(musicFactory.createSong());
        song.setImage(imageFactory.getRandomImage());
        song.setProtectionType(protectionTypes.get(random.nextInt(1, Integer.MAX_VALUE) % protectionTypes.size()));

        User user = users.get(random.nextInt(1, Integer.MAX_VALUE) % users.size());

        synchronized (user){
            song.setUser(user);
            user.getSongs().add(song);
        }


        return song;
    }
}
