package com.musicUpload.dataHandler.models.implementations;

import com.musicUpload.dataHandler.models.CustomEntityInterface;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity(name = "USER_RECOMMENDATION")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"updatedAt", "createdAt"})
public class UserRecommendation implements CustomEntityInterface {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long userId;

    @ManyToOne(fetch = FetchType.EAGER)
    private Song song;

    private int recommendationPosition;

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

    public UserRecommendation(Long userId, Song song, int recommendationPosition) {
        this.userId = userId;
        this.song = song;
        this.recommendationPosition = recommendationPosition;
    }
}
