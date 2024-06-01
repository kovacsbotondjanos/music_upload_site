package com.musicUpload;

import com.musicUpload.dataHandler.seeder.DatabaseSeeder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MusicUploadApplication {

	public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(MusicUploadApplication.class, args);
		DatabaseSeeder dbSeeder = context.getBean(DatabaseSeeder.class);
		//TODO: take this out soon
		dbSeeder.seedDatabaseIfEmpty();
	}
}
