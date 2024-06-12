package com.musicUpload.dataHandler.enums;

import com.musicUpload.exceptions.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum Privilege {
    USER("USER"), PREMIUM_USER("PREMIUM_USER"), ADMIN("ADMIN");

    private final String name;

    public static Privilege getByName(String name) {
        return Arrays.stream(Privilege.values())
                .filter(p -> p.name.equals(name))
                .findAny()
                .orElseThrow(() -> new NotFoundException("ProtectionType with name " + name + "not found"));
    }
}
