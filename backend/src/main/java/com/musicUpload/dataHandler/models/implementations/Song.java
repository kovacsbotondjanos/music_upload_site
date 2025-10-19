package com.musicUpload.dataHandler.models.implementations;

import com.musicUpload.dataHandler.enums.ProtectionType;
import com.musicUpload.dataHandler.models.CustomEntityInterface;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.*;

@Entity
@Data
@Table(
        name = Song.NAME,
        indexes = @Index(columnList = "user_id")
)
@ToString(exclude = {"user", "albums"})
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"protectionType", "user", "albums", "createdAt", "updatedAt"})
@Builder
public class Song implements CustomEntityInterface, Serializable {

    public final static String NAME = "`SONG`";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    @ManyToMany(mappedBy = "songs", fetch = FetchType.LAZY)
    private List<Album> albums = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "tag_song",
            joinColumns = @JoinColumn(name = "song_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"),
            indexes = {
                    @Index(name = "idx_song_id", columnList = "song_id"),
                    @Index(name = "idx_tag_id", columnList = "tag_id")
            }
    )
    private Set<Tag> tags = new HashSet<>();

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

    public void addTags(List<Tag> tags) {
        this.tags.addAll(tags);
    }
}
