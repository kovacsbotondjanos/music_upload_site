package com.musicUpload.util;

import com.musicUpload.dataHandler.services.MinioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
@Slf4j
public class ImageFactory {
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
            log.error(e.getMessage());
        }
        return "";
    }
}
