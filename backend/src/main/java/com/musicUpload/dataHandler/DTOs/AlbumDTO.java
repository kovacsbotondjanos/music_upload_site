package com.musicUpload.dataHandler.DTOs;

import com.musicUpload.dataHandler.models.implementations.Album;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class AlbumDTO {
    private Long id;
    private String name;
    private List<SongDTO> songs;
    private String image;
    private String protectionType;
    private Long userId;
    private String username;
    private Date createdAt;

    public AlbumDTO(Album album, String imageName, Map<String, String> songImageMap) {
        this.id = album.getId();
        this.name = album.getName();
        this.songs = album.getSongs().stream().map(s -> SongDTO.of(s, songImageMap.get(s.getImage()))).toList();
        this.image = imageName;
        this.protectionType = album.getProtectionType().getName();
        this.userId = album.getUser().getId();
        this.username = album.getUser().getUsername();
        this.createdAt = album.getCreatedAt();
    }

    public static AlbumDTO of(Album album, String image, Map<String, String> songImageMap) {
        return new AlbumDTO(album, image, songImageMap);
    }
}
