package com.musicUpload.dataHandler.models.implementations;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Table(
        name = Tag.NAME,
        indexes = @Index(columnList = "name")
)
@NoArgsConstructor
@ToString(exclude = {"songs"})
@EqualsAndHashCode(exclude = {"id", "songs"})
public class Tag {

    public final static String NAME = "`TAG`";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private Set<Song> songs;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;

    public Tag(String name) {
        this.name = name;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }

    public void addSongs(List<Song> songs) {
        this.songs.addAll(songs);
    }

    public void addSongs(Song... songs) {
        addSongs(Arrays.asList(songs));
    }
}
