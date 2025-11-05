package com.musicUpload.musicUpload.recommendationEngine.database.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.*;

@Entity
@Data
@Table(
        name = Song.NAME,
        indexes = {
                @Index(name = "idx_song_protection_type", columnList = "protection_type"),
                @Index(name = "idx_song_user_protection_type", columnList = "user_id, protection_type"),
                @Index(name = "idx_song_protection_type_user", columnList = "protection_type, user_id, id")
        }
)
@ToString(exclude = {"user", "albums"})
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"protectionType", "user", "albums", "createdAt", "updatedAt"})
public class Song implements Serializable {

    public final static String NAME = "`SONG`";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String image;
    private String name;
    private String nameHashed;
    private Long listenCount = 0L;

    @Enumerated(EnumType.STRING)
    private ProtectionType protectionType;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany(mappedBy = "songs", fetch = FetchType.EAGER)
    private List<Album> albums = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "tag_song",
            joinColumns = @JoinColumn(name = "song_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"),
            indexes = {
                    @Index(name = "idx_tag_song_tag_song", columnList = "tag_id, song_id"),
                    @Index(name = "idx_tag_song_song_tag", columnList = "song_id, tag_id")
            }
    )
    private Set<Tag> tags = new HashSet<>();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;
}
