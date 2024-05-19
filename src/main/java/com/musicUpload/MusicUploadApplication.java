package com.musicUpload;

import com.musicUpload.databaseHandler.seeder.DatabaseSeeder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class MusicUploadApplication {

	public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(MusicUploadApplication.class, args);
		DatabaseSeeder dbSeeder = context.getBean(DatabaseSeeder.class);
		dbSeeder.seedDatabaseIfEmpty();
		//TODO: take this out soon
		dbSeeder.seedDatabase();
	}

}
