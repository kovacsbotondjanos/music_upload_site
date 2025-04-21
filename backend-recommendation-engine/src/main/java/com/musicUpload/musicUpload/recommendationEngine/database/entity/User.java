package com.musicUpload.musicUpload.recommendationEngine.database.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = User.NAME)
@ToString(exclude = {"followers", "followedUsers", "albums", "songs"})
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    public final static String NAME = "`USER`";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String profilePicture;
    private String email;
    private String password;
    private String username;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    transient private List<Album> albums = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    transient private List<Song> songs = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Privilege privilege;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_following",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "followed_user_id")
    )
    private List<User> followedUsers = new ArrayList<>();

    @ManyToMany(mappedBy = "followedUsers", fetch = FetchType.EAGER)
    private List<User> followers = new ArrayList<>();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;
}
