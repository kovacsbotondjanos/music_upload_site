package com.musicUpload.util;

import javazoom.jl.decoder.Bitstream;
import com.musicUpload.dataHandler.services.MinioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;

@Service
@Slf4j
public class MusicFactory {
    private final MinioService minioService;
    private final int durationInSeconds = 60;
    private final int sampleRate = 44100;
    private final int numChannels = 2;
    private final int sampleSizeBits = 16;

    public MusicFactory(MinioService minioService) {
        this.minioService = minioService;
    }

    public String generateRandomAudioData() {
        int numSamples = durationInSeconds * sampleRate * numChannels;
        byte[] audioData = new byte[numSamples * (sampleSizeBits / 8)];
        Random random = new Random();

        for (int i = 0; i < audioData.length; i += 2) {
            short sample = (short) (random.nextInt(Short.MAX_VALUE) - (Short.MAX_VALUE / 2));
            byte[] bytes = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(sample).array();
            audioData[i] = bytes[0];
            audioData[i + 1] = bytes[1];
        }

        byte[] mp3Data = convertWavToMp3(audioData);

        MultipartFile multipartFile = new MockMultipartFile("song",
                "",
                "audio/mp3",
                mp3Data);
        return minioService.uploadSong(multipartFile);
    }

    public byte[] convertWavToMp3(byte[] wavData) {
        try {
            ByteArrayInputStream wavInputStream = new ByteArrayInputStream(wavData);

            Bitstream decoder = new Bitstream(
                    wavInputStream
            );

            return decoder.getRawID3v2().readAllBytes();
        } catch (IOException e) {
            log.error("{}", e.getMessage());
            return null;
        }
    }
}
