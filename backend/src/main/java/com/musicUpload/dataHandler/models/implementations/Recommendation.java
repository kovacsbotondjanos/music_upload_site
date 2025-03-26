package com.musicUpload.dataHandler.models.implementations;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@Table(
        name = Recommendation.NAME,
        indexes = {
                @Index(columnList = "user_id")
        }
)
@NoArgsConstructor
public class Recommendation {

    public final static String NAME = "`RECOMMENDATION`";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long songId;
    private Long userId;

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

    public Recommendation(Long songId, Long userId) {
        this.songId = songId;
        this.userId = userId;
    }
}
