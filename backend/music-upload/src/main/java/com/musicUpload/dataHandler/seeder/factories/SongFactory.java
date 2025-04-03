package com.musicUpload.dataHandler.seeder.factories;

import com.github.javafaker.Faker;
import com.musicUpload.dataHandler.enums.ProtectionType;
import com.musicUpload.dataHandler.models.implementations.Song;
import com.musicUpload.dataHandler.models.implementations.Tag;
import com.musicUpload.dataHandler.models.implementations.User;
import com.musicUpload.dataHandler.models.implementations.UserSong;
import com.musicUpload.dataHandler.repositories.SongRepository;
import com.musicUpload.dataHandler.repositories.UserSongRepository;
import com.musicUpload.util.ImageFactory;
import com.musicUpload.util.MusicFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class SongFactory {
    private final ImageFactory imageFactory;
    private final MusicFactory musicFactory;
    private final SongRepository songRepository;
    private final UserSongRepository userSongRepository;

    @Autowired
    public SongFactory(ImageFactory imageFactory,
                       MusicFactory musicFactory,
                       SongRepository songRepository,
                       UserSongRepository userSongRepository) {
        this.imageFactory = imageFactory;
        this.musicFactory = musicFactory;
        this.songRepository = songRepository;
        this.userSongRepository = userSongRepository;
    }

    public List<Song> generateSongs(int number, List<User> users, List<Tag> tags) {
        List<Song> songs = songRepository.saveAll(
                IntStream.range(0, number)
                        .mapToObj(__ -> createSong(users, tags))
                        .toList()
        );

        userSongRepository.saveAll(
                songs.stream()
                        .map(song -> createUserSongs(users, song.getId()))
                        .flatMap(List::stream)
                        .toList()
        );

        return songs;
    }

    private Song createSong(List<User> users, List<Tag> tags) {
        Collections.shuffle(tags);
        Song song = new Song();
        Random random = new Random();

        Faker faker = new Faker(random);
        String name = faker.book().title();

        song.setName(name);
        song.setNameHashed(musicFactory.generateRandomAudioData());
        song.setImage(imageFactory.getRandomImage());
        song.setProtectionType(ProtectionType.getRandomPrivilege());
        song.setListenCount(faker.number().numberBetween(0L, 1_000_000L));
        song.setTags(tags.stream().limit(3).collect(Collectors.toSet()));

        User user = users.get(random.nextInt(0, Integer.MAX_VALUE) % users.size());

        song.setUser(user);

        return song;
    }

    private List<UserSong> createUserSongs(List<User> users, Long songId) {
        Random random = new Random();
        return IntStream.range(0, random.nextInt(0, 5))
                .mapToObj(__ -> {
                    User user = users.get(random.nextInt(0, Integer.MAX_VALUE) % users.size());
                    return new UserSong(songId, user.getId());
                }).toList();
    }
}
