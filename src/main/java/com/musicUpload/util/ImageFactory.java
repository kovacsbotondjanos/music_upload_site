package com.musicUpload.util;

import com.musicUpload.dataHandler.seeder.DatabaseSeeder;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Data
@Service
public class ImageFactory {
    private static final Logger logger = LogManager.getLogger(ImageFactory.class);
    private final String dirName = System.getProperty("user.dir") + FileSystems.getDefault().getSeparator() + "images";

    public void deleteFile(String fileName) {
        File f = new File(dirName + FileSystems.getDefault().getSeparator() + fileName);
        if (f.delete()) {
            logger.info("{} deleted", f.getName());
        }
    }

    public void createImagesDir() {
        try {
            Path dir = Path.of(dirName);
            if (Files.notExists(dir)) {
                Files.createDirectories(dir);
            }
        } catch (IOException ioe) {
            logger.error(ioe.getMessage());
        }
    }

    public String saveFile(ByteArrayInputStream byteArrayInputStream) throws IOException, NullPointerException {
        BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);
        if (bufferedImage == null) {
            throw new NullPointerException("Could not create image from byte array.");
        }

        String fileName = UUID.randomUUID() + ".jpg";
        File outputFile = new File(dirName + FileSystems.getDefault().getSeparator() + fileName);
        ImageIO.write(bufferedImage, "jpg", outputFile);
        return fileName;
    }

    public String getRandomImage() {
        try {
            URL imageUrl = new URL("https://picsum.photos/200");
            HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            try (InputStream inputStream = connection.getInputStream();
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[200];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                byte[] imageBytes = outputStream.toByteArray();
                return saveFile(new ByteArrayInputStream(imageBytes));
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return "";
    }
}
