package com.musicUpload.util;

import lombok.Data;
import org.springframework.stereotype.Service;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.UUID;

@Service
@Data
public class MusicFactory {
    private final String dirName = System.getProperty("user.dir") + FileSystems.getDefault().getSeparator() + "music";
    private final int durationInSeconds = 60;
    private final int sampleRate = 44100;
    private final int numChannels = 2;
    private final int sampleSizeBits = 16;

    public void createMusicDir() {
        try {
            Path dir = Path.of(dirName);
            if (Files.notExists(dir)) {
                Files.createDirectories(dir);
            }
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }
    }

    public void deleteFile(String fileName) {
        File f = new File(dirName + FileSystems.getDefault().getSeparator() + fileName);
        if (f.delete()) {
            System.out.println(f.getName() + " deleted");
        }
    }

    public String createSong() {
        return saveAsMP3(generateRandomAudioData());
    }

    private String saveAsMP3(byte[] audioData) {
        //TODO: actually encode the file to mp3 format
        try {
            AudioFormat format = new AudioFormat(sampleRate, sampleSizeBits, numChannels, true, false);
            ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
            AudioInputStream audioInputStream = new AudioInputStream(bais, format, audioData.length / format.getFrameSize());

            String fileName = UUID.randomUUID() + ".mp3";
            Path outputFile = Paths.get(dirName + FileSystems.getDefault().getSeparator() + fileName);

            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, outputFile.toFile());
            return fileName;

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return " ";
    }

    private byte[] generateRandomAudioData() {
        int numSamples = durationInSeconds * sampleRate * numChannels;
        byte[] audioData = new byte[numSamples * (sampleSizeBits / 8)];
        Random random = new Random();

        for (int i = 0; i < audioData.length; i += 2) {
            short sample = (short) (random.nextInt(Short.MAX_VALUE) - (Short.MAX_VALUE / 2));
            byte[] bytes = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(sample).array();
            audioData[i] = bytes[0];
            audioData[i + 1] = bytes[1];
        }

        return audioData;
    }
}
