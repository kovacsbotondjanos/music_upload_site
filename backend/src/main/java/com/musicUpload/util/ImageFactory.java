package com.musicUpload.util;

import com.musicUpload.dataHandler.services.MinioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageFactory {
    private final MinioService minioService;
    private final String URL = "https://ui-avatars.com/api/?name=%s&size=200&format=png&background=%s&color=%s";
    private final String bgColor = "000000";
    private final String textColor = "00FF00";

    public String getRandomImage(String name) {
        try {
            String url = String.format(URL, URLEncoder.encode(name, StandardCharsets.UTF_8), bgColor, textColor);
            URL imageUrl = new URL(url);
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
