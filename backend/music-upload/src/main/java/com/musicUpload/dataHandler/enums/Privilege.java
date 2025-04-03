package com.musicUpload.dataHandler.enums;

import com.musicUpload.exceptions.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum Privilege implements GrantedAuthority {
    USER, PREMIUM_USER, ADMIN;

    public static Privilege getByName(String name) {
        return Arrays.stream(Privilege.values())
                .filter(p -> p.name().equals(name))
                .findAny()
                .orElseThrow(() -> new NotFoundException("ProtectionType with name " + name + "not found"));
    }

    @Override
    public String getAuthority() {
        return this.name();
    }
}
