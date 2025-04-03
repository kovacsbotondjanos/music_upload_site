package com.musicUpload.dataHandler.services;

import com.musicUpload.dataHandler.DTOs.TagDTO;
import com.musicUpload.dataHandler.models.implementations.Tag;
import com.musicUpload.dataHandler.repositories.TagRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {
    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public Tag saveTag(String tag) {
        return tagRepository.save(new Tag(tag.toUpperCase()));
    }

    public List<Tag> findByIdsIn(List<String> names) {
        //since we can only add 3 tags to each song i think it's ok:)
        return names.stream()
                .map(name -> tagRepository.findByName(name.toUpperCase())
                        .orElseGet(() -> saveTag(name))
                )
                .toList();
    }

    public List<TagDTO> searchByName(String name) {
        return tagRepository.findByNameLike(name.toUpperCase()).stream()
                .map(TagDTO::of)
                .toList();
    }
}
