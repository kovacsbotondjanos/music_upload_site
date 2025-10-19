package com.musicUpload.dataHandler.DTOs;

import com.musicUpload.dataHandler.models.implementations.Album;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlbumCardDTO {
    private Long id;
    private String name;
    private String image;
    private String protectionType;
    private Long userId;
    private Date createdAt;

    public AlbumCardDTO(Album album, String image) {
        this.id = album.getId();
        this.name = album.getName();
        this.image = image;
        this.protectionType = album.getProtectionType().getName();
        this.userId = album.getUser().getId();
        this.createdAt = album.getCreatedAt();
    }

    public static AlbumCardDTO of(Album album, String image) {
        return new AlbumCardDTO(album, image);
    }
}
