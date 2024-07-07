package com.musicUpload.dataHandler.services;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service
public class MinioService {
    private static final Logger logger = LogManager.getLogger(MinioService.class);
    private final MinioClient minioClient;

    @Value("${minio.songBucketName}")
    private String songBucket;

    @Value("${minio.pictureBucketName}")
    private String imageBucket;

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

    private void deleteFile(String name, String bucketName) {

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
            return fileName + "." + file.getContentType();
        } catch (ErrorResponseException | NoSuchAlgorithmException | InsufficientDataException | InternalException |
                 InvalidKeyException | InvalidResponseException | IOException | ServerException | XmlParserException e) {
            //TODO: handle this better lol
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
