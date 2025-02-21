package com.musicUpload.dataHandler.models.implementations;

import com.musicUpload.dataHandler.models.CustomEntityInterface;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@Table(name = UserSong.NAME)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"updatedAt", "createdAt"})
public class UserSong implements CustomEntityInterface {

    public final static String NAME = "`USER_SONG`";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long listenCount;
    private Long userId;
    private Long songId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;

    public UserSong(Long songId, Long userId, Long listenCount) {
        this.userId = userId;
        this.songId = songId;
        this.listenCount = listenCount;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }
}
