package com.musicUpload.dataHandler.DTOs;

import com.musicUpload.dataHandler.models.implementations.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TagDTO {
    Long id;
    String name;
    public static TagDTO of(Tag tag) {
        return new TagDTO(
                tag.getId(),
                tag.getName()
        );
    }
}
