package com.musicUpload.musicUpload.recommendationEngine.database.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(
        name = Album.NAME,
        indexes = {
                @Index(columnList = "user_id")
        }
)
@ToString(exclude = {"user", "songs"})
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"protectionType", "user", "songs", "createdAt", "updatedAt"})
public class Album implements Serializable {

    public final static String NAME = "`ALBUM`";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String image;
    private String name;

    @Enumerated(EnumType.STRING)
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
}
