package com.musicUpload.util;

import com.musicUpload.dataHandler.services.MinioService;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.FileSystems;

@Service
public class ImageFactory {
    private static final Logger logger = LogManager.getLogger(ImageFactory.class);
    private final MinioService minioService;

    public ImageFactory(MinioService minioService) {
        this.minioService = minioService;
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
                MultipartFile multipartFile = new MockMultipartFile("image",
                                                                    "",
                                                                    "image/jpeg",
                                                                    imageBytes);
                return minioService.uploadImage(multipartFile);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return "";
    }
}
