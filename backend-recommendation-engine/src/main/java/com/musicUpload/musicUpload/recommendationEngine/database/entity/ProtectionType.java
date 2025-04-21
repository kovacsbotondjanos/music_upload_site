package com.musicUpload.musicUpload.recommendationEngine.database.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProtectionType {
    PRIVATE("PRIVATE"), PROTECTED("PROTECTED"), PUBLIC("PUBLIC");

    private final String name;
}
