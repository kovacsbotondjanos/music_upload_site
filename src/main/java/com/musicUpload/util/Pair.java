package com.musicUpload.util;

import lombok.*;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Pair<S, T> {
    private S first;
    private T second;

    public static<S, T> Pair<S, T> of(S first, T second) {
        return new Pair<>(first, second);
    }
}
