package com.musicUpload.musicUpload.recommendationEngine.database.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Privilege {
    USER, PREMIUM_USER, ADMIN;
}
