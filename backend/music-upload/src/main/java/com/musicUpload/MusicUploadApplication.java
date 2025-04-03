package com.musicUpload;

import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "3m")
@Slf4j
public class MusicUploadApplication {

    public static void main(String[] args) {
        SpringApplication.run(MusicUploadApplication.class, args);
        log.info("Application started");
    }
}
