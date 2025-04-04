package com.musicUpload.dataHandler.services;

import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class MinioService {
    private final MinioClient minioClient;

    @Value("${minio.songBucketName}")
    private String songBucket;

    @Value("${minio.pictureBucketName}")
    private String imageBucket;

    @Value("${linkExpirationTime}")
    private int expirationTime;

    @Autowired
    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public String uploadSong(MultipartFile file) {
        return uploadFile(file, songBucket);
    }

    public String uploadImage(MultipartFile file) {
        return uploadFile(file, imageBucket);
    }

    public void deleteSong(String name) {
        deleteFile(name, songBucket);
    }

    public void deleteImage(String name) {
        deleteFile(name, imageBucket);
    }

    public String getImage(String name) {
        return getUrlToContent(name, imageBucket);
    }

    public String getSong(String name) {
        return getUrlToContent(name, songBucket);
    }

    private void deleteFile(String name,
                            String bucketName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(name)
                    .build());
        } catch (MinioException | InvalidKeyException |
                 IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String getUrlToContent(String name,
                                   String bucketName) {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .bucket(bucketName)
                    .method(Method.GET)
                    .object(name)
                    .expiry(expirationTime, TimeUnit.SECONDS)
                    .build());
        } catch (MinioException | InvalidKeyException |
                 IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String uploadFile(MultipartFile file,
                              String bucketName) {
        try {
            boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!isExist) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
            String fileName = UUID.randomUUID().toString();
            InputStream inputStream = file.getInputStream();
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .stream(inputStream,
                            inputStream.available(),
                            -1)
                    .contentType(file.getContentType())
                    .build());
            return fileName;
        } catch (MinioException | NoSuchAlgorithmException |
                 InvalidKeyException | IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
