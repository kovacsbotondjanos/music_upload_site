package com.musicUpload.controllers;

import com.musicUpload.dataHandler.DTOs.TagDTO;
import com.musicUpload.dataHandler.services.TagService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/tags")
@CrossOrigin
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/search/{name}")
    public List<TagDTO> findTag(@PathVariable("name") String name) {
        return tagService.searchByName(name);
    }
}
