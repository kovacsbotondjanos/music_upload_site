package com.musicUpload.dataHandler.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SongCreateAndPatchDTO {
    private String protectionType;
    private String name;
}
