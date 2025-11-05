package com.musicUpload.musicUpload.recommendationEngine.database.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Data
@Builder
@Table(
        name = UserSong.NAME,
        indexes = {
                @Index(name = "idx_user_song_created_song_user", columnList = "created_at, song_id, user_id"),
                @Index(name = "idx_user_song_song_user_created", columnList = "song_id, user_id, created_at"),
                @Index(name = "idx_user_song_created_at", columnList = "created_at")
        }
)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"id", "updatedAt", "createdAt"})
public class UserSong {

    public final static String NAME = "`USER_SONG`";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long songId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;

    @PrePersist
    public void onPersist() {
        createdAt = new Date();
        updatedAt = new Date();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = new Date();
    }

    public UserSong(Long songId, Long userId, Date date) {
        this.userId = userId;
        this.songId = songId;
        this.createdAt = date;
    }

    public UserSong(Long songId, Long userId) {
        this(songId, userId, new Date());
    }
}
