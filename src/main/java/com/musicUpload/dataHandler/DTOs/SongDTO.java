package com.musicUpload.dataHandler.DTOs;

import com.musicUpload.dataHandler.models.Album;
import com.musicUpload.dataHandler.models.Song;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SongDTO {
    private Long id;
    private String image;
    private String name;
    private String nameHashed;
    private String protectionType;
    private Long userId;
    private List<Long> albumsIds;
    private Date createdAt;

    public SongDTO(Song song){
        this.id = song.getId();
        this.image = song.getImage();
        this.name = song.getName();
        this.nameHashed = song.getNameHashed();
        this.protectionType = song.getProtectionType().getName();
        this.userId = song.getUser().getId();
        this.albumsIds = song.getAlbums().stream().map(Album::getId).toList();
        this.createdAt = song.getCreatedAt();
    }

    public static SongDTO of(Song song){
        return new SongDTO(song);
    }
}
