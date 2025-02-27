package com.musicUpload.dataHandler.models.implementations;

import com.musicUpload.dataHandler.enums.ProtectionType;
import com.musicUpload.dataHandler.models.CustomEntityInterface;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = Album.NAME)
@ToString(exclude = {"user", "songs"})
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"protectionType", "user", "songs", "createdAt", "updatedAt"})
public class Album implements CustomEntityInterface, Serializable {

    public final static String NAME = "`ALBUM`";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String image;
    private String name;

    @Enumerated(EnumType.ORDINAL)
    private ProtectionType protectionType;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "song_album",
            joinColumns = @JoinColumn(name = "album_id"),
            inverseJoinColumns = @JoinColumn(name = "song_id")
    )
    private List<Song> songs = new ArrayList<>();

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
