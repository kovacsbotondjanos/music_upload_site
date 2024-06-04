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
}
