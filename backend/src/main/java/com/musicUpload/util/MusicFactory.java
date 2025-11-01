package com.musicUpload.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
public class MusicFactory {

    public MultipartFile generateAudio() {
        try (InputStream inputStream = getClass().getResourceAsStream("/seeder/songs/sample.mp3")) {
            byte[] fileBytes = inputStream.readAllBytes();

            return new MockMultipartFile(
                    "song",
                    "sample.mp3",
                    "audio/mp3",
                    fileBytes
            );
        } catch (IOException e) {
            log.error("Failed to read mp3 file");
            return null;
        }
    }
}
