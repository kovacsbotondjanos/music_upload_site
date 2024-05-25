package com.musicUpload.dataHandler.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@ToString(exclude = {"users"})
public class Auth {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Long id;

    @Expose
    private String name;

    @OneToMany(mappedBy = "authority", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<User> users = new ArrayList<>();
}
