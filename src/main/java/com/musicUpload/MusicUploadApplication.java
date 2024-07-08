package com.musicUpload;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;

@SpringBootApplication
@EnableScheduling
@EnableJdbcHttpSession
public class MusicUploadApplication {
    private static final Logger logger = LogManager.getLogger(MusicUploadApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(MusicUploadApplication.class, args);
        logger.info("Application started");
    }
}
