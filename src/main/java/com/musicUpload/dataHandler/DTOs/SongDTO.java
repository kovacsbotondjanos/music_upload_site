package com.musicUpload.dataHandler.DTOs;

import com.musicUpload.dataHandler.models.Song;
import lombok.Data;

import java.util.Date;

@Data
public class SongDTO {
    private Long id;
    private String image;
    private String name;
    private String nameHashed;
    private String protectionType;
    private Long userId;
    private Date createdAt;
    private Long listenCount;

    public SongDTO(Song song){
        this.id = song.getId();
        this.image = song.getImage();
        this.name = song.getName();
        this.nameHashed = song.getNameHashed();
        this.protectionType = song.getProtectionType().getName();
        this.userId = song.getUser().getId();
        this.createdAt = song.getCreatedAt();
        this.listenCount = song.getListenCount();
    }

    public static SongDTO of(Song song){
        return new SongDTO(song);
    }
}
