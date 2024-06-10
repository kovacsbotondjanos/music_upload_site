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
@Table(name = "USERS_SONGS")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"updatedAt", "createdAt"})
public class UserSong implements CustomEntityInterface {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long listenCount;
    private Long userId;
    private Long songId;
    //TODO: user createdAt for this
    private int year;
    private int month;

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

    public UserSong(Long songId, Long userId, Long listenCount, int year, int month) {
        this.userId = userId;
        this.songId = songId;
        this.listenCount = listenCount;
        this.year = year;
        this.month = month;
    }
}
