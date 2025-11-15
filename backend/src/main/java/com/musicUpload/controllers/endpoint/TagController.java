package com.musicUpload.controllers.endpoint;

import com.musicUpload.dataHandler.DTOs.TagDTO;
import com.musicUpload.dataHandler.services.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/tags")
@CrossOrigin
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping("/search/{name}")
    public List<TagDTO> findTag(@PathVariable("name") String name) {
        return tagService.searchByName(name);
    }
}
