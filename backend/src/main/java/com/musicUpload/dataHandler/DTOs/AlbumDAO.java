package com.musicUpload.dataHandler.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlbumDAO {
    private String protectionType;
    private String name;
    private List<Long> songIds;
}
