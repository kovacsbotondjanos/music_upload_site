package com.musicUpload.dataHandler.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Random;

@Getter
@AllArgsConstructor
public enum ProtectionType {
    PRIVATE("PRIVATE"), PROTECTED("PROTECTED"), PUBLIC("PUBLIC");

    private final String name;

    public static ProtectionType getRandomPrivilege() {
        var vals = ProtectionType.values();
        return vals[new Random().nextInt(0, Integer.MAX_VALUE) % vals.length];
    }
}
