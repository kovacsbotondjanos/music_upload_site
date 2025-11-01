package com.musicUpload.dataHandler.models.implementations;

import com.musicUpload.dataHandler.models.CustomEntityInterface;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Data
@Builder
@Table(
        name = UserSong.NAME,
        indexes = {
                @Index(name = "idx_user_song_created_song_user", columnList = "created_at, songId, userId"),
                @Index(name = "idx_user_song_song_user_created", columnList = "songId, userId, created_at")
        }
)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"id", "updatedAt", "createdAt"})
public class UserSong implements CustomEntityInterface {

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

    public UserSong(Long songId, Long userId) {
        this.userId = userId;
        this.songId = songId;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }

    @PrePersist
    protected void onCreate() {
        updatedAt = new Date();
        createdAt = new Date();
    }
}
