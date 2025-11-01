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
        int rand = new Random().nextInt(0, Integer.MAX_VALUE);
        if (rand % 10 == 0 || rand % 7 == 0) {
            return PROTECTED;
        } else if (rand % 3 == 0) {
            return PRIVATE;
        } else {
            return PUBLIC;
        }
    }
}
