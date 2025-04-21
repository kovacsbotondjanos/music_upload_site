package com.musicUpload.musicUpload.recommendationEngine.database.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@Table(
        name = UserSong.NAME,
        indexes = {
                @Index(columnList = "userId"),
                @Index(columnList = "songId")
        }
)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"id", "updatedAt", "createdAt"})
public class UserSong {

    public final static String NAME = "`USER_SONG`";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long userId;
    private Long songId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;
}
