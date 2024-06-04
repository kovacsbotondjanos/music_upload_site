package com.musicUpload.dataHandler.models.implementations;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.musicUpload.dataHandler.models.CustomEntityInterface;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "SONGS")
@ToString(exclude = {"user", "albums"})
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"protectionType", "user", "albums"})
public class Song implements CustomEntityInterface {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String image;
    private String name;
    private String nameHashed;
    private Long listenCount = 0L;

    @ManyToOne
    @JoinColumn(name = "protection_id")
    private ProtectionType protectionType;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany(mappedBy = "songs", fetch = FetchType.EAGER)
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

    public void addListen() {
        listenCount++;
    }

    public void addListen(Long count) {
        listenCount += count;
    }

    public long getCacheIndex() {
        if(listenCount == 0) {
            return 0;
        }
        //I take the 100 base log of the listen count to determine how long i have to cache the song, the cap is one hour
        //TODO: write a better method than this, maybe take sqrt of this number?
        return Math.min(1000 * 60 * 60, (long) (1000 * 60 * Math.log(listenCount) / Math.log(100)));
    }
}
