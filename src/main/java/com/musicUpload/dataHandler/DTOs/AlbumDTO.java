package com.musicUpload.dataHandler.DTOs;

import com.musicUpload.dataHandler.models.implementations.Album;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class AlbumDTO {
    private Long id;
    private String name;
    private List<SongDTO> songs;
    private String image;
    private String protectionType;
    private Long userId;
    private Date createdAt;

    public AlbumDTO(Album album){
        this.id = album.getId();
        this.name = album.getName();
        this.songs = album.getSongs().stream().map(SongDTO::new).toList();
        this.image = album.getImage();
        this.protectionType = album.getProtectionType().getName();
        this.userId = album.getUser().getId();
        this.createdAt = album.getCreatedAt();
    }

    public static AlbumDTO of(Album album){
        return new AlbumDTO(album);
    }
}
