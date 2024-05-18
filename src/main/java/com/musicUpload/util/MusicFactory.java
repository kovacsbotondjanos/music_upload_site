package com.musicUpload.util;

import de.maxhenkel.lame4j.DecodedAudio;
import de.maxhenkel.lame4j.Mp3Decoder;
import de.maxhenkel.lame4j.Mp3Encoder;
import de.maxhenkel.lame4j.UnknownPlatformException;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Random;
import java.util.UUID;

@Service
public class MusicFactory {
    private final String dirName = System.getProperty("user.dir") + "//music";
    private final int durationInSeconds = 120;
    private final int sampleRate = 44100;
    private final int numChannels = 2;

    public void createMusicDir(){
        try{
            Path dir = Path.of(dirName);
            if (Files.notExists(dir)) {
                Files.createDirectories(dir);
            }
        }
        catch (IOException ioe){
            System.err.println(ioe.getMessage());
        }
    }

    public String createRandomSong() {
        try{
            InputStream inputStream = new ByteArrayInputStream(generateRandomAudioData());
            DecodedAudio decodedAudio = Mp3Decoder.decode(inputStream);

            short[] decode = decodedAudio.getSamples();

            String fileName = UUID.randomUUID() + ".mp3";
            Path outputFile = Paths.get(dirName + "//" + fileName);

            if (!Files.exists(outputFile)) {
                Files.createFile(outputFile);
            }

            OutputStream outputStream = Files.newOutputStream(outputFile, StandardOpenOption.CREATE);

            Mp3Encoder encoder = new Mp3Encoder(decodedAudio.getChannelCount(), decodedAudio.getSampleRate(),
                    decodedAudio.getBitRate(), 5, outputStream);

            encoder.write(decode);
            encoder.close();

            return fileName;
        }
        catch(IOException | UnknownPlatformException e){
            System.out.println(e.getMessage());
        }

        return "";
    }

    private byte[] generateRandomAudioData() {
        int numSamples = (durationInSeconds * sampleRate * numChannels);
        byte[] audioData = new byte[numSamples + 44];
        Random random = new Random();

        audioData[0] = 'R';  // RIFF/WAVE header
        audioData[1] = 'I';
        audioData[2] = 'F';
        audioData[3] = 'F';
        audioData[4] = (byte) (numSamples & 0xff);
        audioData[5] = (byte) ((numSamples >> 8) & 0xff);
        audioData[6] = (byte) ((numSamples >> 16) & 0xff);
        audioData[7] = (byte) ((numSamples >> 24) & 0xff);
        audioData[8] = 'W';
        audioData[9] = 'A';
        audioData[10] = 'V';
        audioData[11] = 'E';
        audioData[12] = 'f';  // 'fmt ' chunk
        audioData[13] = 'm';
        audioData[14] = 't';
        audioData[15] = ' ';
        audioData[16] = 16;  // 4 bytes: size of 'fmt ' chunk
        audioData[17] = 0;
        audioData[18] = 0;
        audioData[19] = 0;
        audioData[20] = 1;  // format = 1
        audioData[21] = 0;
        audioData[22] = (byte) numChannels;
        audioData[23] = 0;
        audioData[24] = (byte) (sampleRate & 0xff);
        audioData[25] = (byte) ((sampleRate >> 8) & 0xff);
        audioData[26] = (byte) ((sampleRate >> 16) & 0xff);
        audioData[27] = (byte) ((sampleRate >> 24) & 0xff);
        audioData[28] = (byte) (sampleRate & 0xff);
        audioData[29] = (byte) ((sampleRate >> 8) & 0xff);
        audioData[30] = (byte) ((sampleRate >> 16) & 0xff);
        audioData[31] = (byte) ((sampleRate >> 24) & 0xff);
        audioData[32] = (byte) (2 * 16 / 8);  // block align
        audioData[33] = 0;
        audioData[34] = (byte) numSamples;  // bits per sample
        audioData[35] = 0;
        audioData[36] = 'd';
        audioData[37] = 'a';
        audioData[38] = 't';
        audioData[39] = 'a';
        audioData[40] = (byte) (durationInSeconds & 0xff);
        audioData[41] = (byte) ((durationInSeconds >> 8) & 0xff);
        audioData[42] = (byte) ((durationInSeconds >> 16) & 0xff);
        audioData[43] = (byte) ((durationInSeconds >> 24) & 0xff);

        for (int i = 44; i < numSamples; i++) {
            audioData[i] = (byte) (random.nextInt(Short.MAX_VALUE) - Short.MAX_VALUE / 2);
        }

        return audioData;
    }
}
