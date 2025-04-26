package com.musicUpload.util;

import lombok.*;

import java.util.Map;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Pair<S, T> {
    private S first;
    private T second;

    public Pair(Map.Entry<S, T> entry) {
        first = entry.getKey();
        second = entry.getValue();
    }

    public static <S, T> Pair<S, T> of(S first, T second) {
        return new Pair<>(first, second);
    }
}
