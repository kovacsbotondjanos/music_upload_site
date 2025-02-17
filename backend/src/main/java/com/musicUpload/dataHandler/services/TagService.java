package com.musicUpload.dataHandler.services;

import com.musicUpload.dataHandler.models.implementations.Tag;
import com.musicUpload.dataHandler.repositories.TagRepository;

import java.util.List;

public class TagService {
    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public void saveTag(String tag) {

    }

    public List<Tag> findByIdsIn(List<String> names) {
        //since we can only add 3 tags to each song i think it's ok:)
        return names.stream()
            .map(name -> tagRepository.findByNameIgnoreCase(name)
                            .orElse(tagRepository.save(new Tag(name.toUpperCase())))
            )
            .toList();
    }
}
