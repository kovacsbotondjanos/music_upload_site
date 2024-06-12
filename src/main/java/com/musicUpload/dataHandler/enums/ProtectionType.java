package com.musicUpload.dataHandler.enums;

import com.musicUpload.exceptions.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Random;

@Getter
@AllArgsConstructor
public enum ProtectionType {
    PRIVATE("PRIVATE"), PROTECTED("PROTECTED"), PUBLIC("PUBLIC");

    private final String name;

    public static ProtectionType getByName(String name) {
        return Arrays.stream(ProtectionType.values())
                .filter(p -> p.name.equals(name))
                .findAny()
                .orElseThrow(() -> new NotFoundException("ProtectionType with name " + name + "not found"));
    }

    public static ProtectionType getRandomPrivilege() {
        var vals = ProtectionType.values();
        return vals[new Random().nextInt(0, Integer.MAX_VALUE) % vals.length];
    }
}
