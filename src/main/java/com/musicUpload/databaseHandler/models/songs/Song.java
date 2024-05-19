package com.musicUpload.databaseHandler.models.songs;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.musicUpload.databaseHandler.models.albums.Album;
import com.musicUpload.databaseHandler.models.protectionType.ProtectionType;
import com.musicUpload.databaseHandler.models.users.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "SONGS")
@ToString(exclude = {"user", "albums"})
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String image;
    private String name;
    private String nameHashed;

    @ManyToOne
    @JoinColumn(name = "protection_id")
    private ProtectionType protectionType;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany(mappedBy = "songs")
    @JsonIgnore
    private List<Album> albums = new ArrayList<>();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }
}
