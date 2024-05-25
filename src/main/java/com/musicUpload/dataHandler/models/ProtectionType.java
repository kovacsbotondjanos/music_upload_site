package com.musicUpload.dataHandler.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@ToString(exclude = {"id", "songs", "albums"})
public class ProtectionType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Long id;

    @Expose
    private String name;

    @OneToMany(mappedBy = "protectionType", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Song> songs = new ArrayList<>();

    @OneToMany(mappedBy = "protectionType", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Album> albums = new ArrayList<>();
}
