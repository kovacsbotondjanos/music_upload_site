package com.musicUpload.dataHandler.models.implementations;

import com.musicUpload.dataHandler.enums.ProtectionType;
import com.musicUpload.dataHandler.models.CustomEntityInterface;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.*;

@Entity
@Data
@Table(name = Song.NAME)
@ToString(exclude = {"user", "albums"})
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"protectionType", "user", "albums", "createdAt", "updatedAt"})
public class Song implements CustomEntityInterface, Serializable {

    public final static String NAME = "SONG";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String image;
    private String name;
    private String nameHashed;
    private Long listenCount = 0L;

    @Enumerated(EnumType.ORDINAL)
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
                @Index(name = "idx_song_id", columnList = "song_id"),
                @Index(name = "idx_tag_id", columnList = "tag_id")
            }
    )
    private Set<Tag> tags;

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

    public void addListen(Long count) {
        listenCount += count;
    }

    public void addTags(List<Tag> tags) {
        this.tags.addAll(tags);
    }

    public void addTags(Tag... tags) {
        addTags(Arrays.asList(tags));
    }

    public long getCacheIndex() {
        if (listenCount == 0) {
            return 0;
        }
        //I take the 100 base log of the listen count to determine how long i have to cache the song, the cap is one hour
        return (long) (Math.min(1000 * 60 * 60, 100 * Math.log(listenCount) / Math.log(100)) * Math.sqrt(listenCount));
    }
}
