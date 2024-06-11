package com.musicUpload.dataHandler.seeder.factories;

import com.github.javafaker.Faker;
import com.musicUpload.dataHandler.models.implementations.ProtectionType;
import com.musicUpload.dataHandler.models.implementations.Song;
import com.musicUpload.dataHandler.models.implementations.User;
import com.musicUpload.dataHandler.services.SongService;
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
    private final ImageFactory imageFactory;
    private final MusicFactory musicFactory;
    private final SongService songService;

    @Autowired
    public SongFactory(ImageFactory imageFactory, MusicFactory musicFactory, SongService songService) {
        this.imageFactory = imageFactory;
        this.musicFactory = musicFactory;
        this.songService = songService;
        musicFactory.createMusicDir();
    }

    public List<Song> generateSongs(int number, List<User> users, List<ProtectionType> protectionTypes) {
        List<Song> songs = new CopyOnWriteArrayList<>();

        IntStream.range(0, number).parallel().forEachOrdered(__ -> {
            Song song = createSong(users, protectionTypes);
            songs.add(song);
            songService.addSong(song);
        });

        return songs;
    }

    private Song createSong(List<User> users, List<ProtectionType> protectionTypes) {
        Song song = new Song();
        Random random = new Random();

        Faker faker = new Faker(random);
        String name = faker.book().title();

        song.setName(name);
        song.setNameHashed(musicFactory.createSong());
        song.setImage(imageFactory.getRandomImage());
        song.setProtectionType(protectionTypes.get(random.nextInt(1, Integer.MAX_VALUE) % protectionTypes.size()));
        song.setListenCount(faker.number().numberBetween(0L, 1_000_000L));

        User user = users.get(random.nextInt(0, Integer.MAX_VALUE) % users.size());

        synchronized (user) {
            song.setUser(user);
            user.getSongs().add(song);
        }


        return song;
    }
}
