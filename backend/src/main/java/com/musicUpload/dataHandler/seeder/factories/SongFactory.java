package com.musicUpload.dataHandler.seeder.factories;

import com.github.javafaker.Faker;
import com.musicUpload.dataHandler.enums.ProtectionType;
import com.musicUpload.dataHandler.models.implementations.Song;
import com.musicUpload.dataHandler.models.implementations.User;
import com.musicUpload.dataHandler.services.SongService;
import com.musicUpload.util.ImageFactory;
import com.musicUpload.util.MusicFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
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
    }

    public List<Song> generateSongs(int number, List<User> users, ExecutorService executorService) {
        List<Song> songs = Collections.synchronizedList(new ArrayList<>());

        IntStream.range(0, number).parallel().forEachOrdered(__ ->
            executorService.submit(() -> {
                Song song = createSong(users);
                songs.add(song);
                songService.addSong(song);
            })
        );

        return songs;
    }

    private Song createSong(List<User> users) {
        Song song = new Song();
        Random random = new Random();

        Faker faker = new Faker(random);
        String name = faker.book().title();

        song.setName(name);
        song.setNameHashed(musicFactory.generateRandomAudioData());
        song.setImage(imageFactory.getRandomImage());
        song.setProtectionType(ProtectionType.getRandomPrivilege());
        song.setListenCount(faker.number().numberBetween(0L, 1_000_000L));

        User user = users.get(random.nextInt(0, Integer.MAX_VALUE) % users.size());

        synchronized (user) {
            song.setUser(user);
            user.getSongs().add(song);
        }

        return song;
    }
}
