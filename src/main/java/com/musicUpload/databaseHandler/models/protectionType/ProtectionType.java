package com.musicUpload.databaseHandler.models.protectionType;

import com.google.gson.annotations.Expose;
import com.musicUpload.databaseHandler.models.albums.Album;
import com.musicUpload.databaseHandler.models.songs.Song;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class ProtectionType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Expose
    private String name;

    @OneToMany(mappedBy = "protectionType", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Song> songs = new ArrayList<>();

    @OneToMany(mappedBy = "protectionType", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Album> albums = new ArrayList<>();
}
