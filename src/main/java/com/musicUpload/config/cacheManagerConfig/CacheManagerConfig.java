package com.musicUpload.config.cacheManagerConfig;

import com.musicUpload.cronJobs.EntityCacheManager;
import com.musicUpload.dataHandler.models.implementations.Album;
import com.musicUpload.dataHandler.models.implementations.Song;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheManagerConfig {

    @Bean
    public EntityCacheManager<Song> songCache() {
        return new EntityCacheManager<>();
    }

    @Bean
    public EntityCacheManager<Album> albumCache() {
        return new EntityCacheManager<>();
    }
}
