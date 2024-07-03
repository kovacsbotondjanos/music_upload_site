package com.musicUpload.dataHandler.services;

import com.musicUpload.util.ImageFactory;
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
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

    private String uploadFile(String bucketName,
                              String contentType,
                              ByteArrayInputStream inputStream,
                              long size) {
        try {
            boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!isExist) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
            String fileName = UUID.randomUUID().toString();
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .stream(inputStream, size, -1)
                    .contentType(contentType)
                    .build());
            return fileName + "." + contentType;
        } catch (ErrorResponseException | NoSuchAlgorithmException | InsufficientDataException | InternalException |
                 InvalidKeyException | InvalidResponseException | IOException | ServerException | XmlParserException e) {
            //TODO: handle this better lol
            logger.info(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public String uploadSong(ByteArrayInputStream inputStream,
                             long size) {
        return uploadFile(songBucket, "mp3", inputStream, size);
    }

    public String uploadImage(ByteArrayInputStream inputStream,
                              long size) {
        return uploadFile(imageBucket, "jpg", inputStream, size);
    }
}
