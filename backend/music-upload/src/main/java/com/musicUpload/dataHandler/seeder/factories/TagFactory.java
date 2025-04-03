package com.musicUpload.dataHandler.seeder.factories;

import com.github.javafaker.Faker;
import com.musicUpload.dataHandler.models.implementations.Tag;
import com.musicUpload.dataHandler.repositories.TagRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class TagFactory {

    private final TagRepository tagRepository;

    public TagFactory(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public List<Tag> initTags(int number) {
        Set<String> tagNames = new HashSet<>();
        return tagRepository.saveAll(IntStream.range(0, number)
                .mapToObj(__ -> createTag(tagNames))
                .collect(Collectors.toSet())
        );
    }

    private Tag createTag(Set<String> tagNames) {
        Tag tag = new Tag();
        Random rand = new Random();

        Faker faker = new Faker(rand);
        String name = faker.music().genre().toUpperCase();

        while (tagNames.contains(name)) {
            name = faker.music().genre().toUpperCase();
        }

        tagNames.add(name);
        tag.setName(name);

        return tag;
    }
}
