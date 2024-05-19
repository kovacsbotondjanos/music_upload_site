package com.musicUpload.databaseHandler.models.users;

import com.google.gson.annotations.Expose;
import com.musicUpload.databaseHandler.models.albums.Album;
import com.musicUpload.databaseHandler.models.auth.Auth;
import com.musicUpload.databaseHandler.models.songs.Song;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "USERS")
@ToString(exclude = {"followers", "followedUsers"})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String profilePicture;
    @Expose
    private String email;
    @Expose
    private String password;
    @Expose
    private String username;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Album> albums = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Song> songs = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "auth_id")
    private Auth authority;

    @ManyToMany
    @JoinTable(
            name = "user_following",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "followed_user_id")
    )
    private List<User> followedUsers = new ArrayList<>();

    @ManyToMany(mappedBy = "followedUsers")
    private List<User> followers = new ArrayList<>();

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
}
