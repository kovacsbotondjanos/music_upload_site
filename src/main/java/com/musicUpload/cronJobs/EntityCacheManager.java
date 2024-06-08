package com.musicUpload.cronJobs;

import com.musicUpload.dataHandler.models.CustomEntityInterface;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class EntityCacheManager<T extends CustomEntityInterface> {
    private static final Logger logger = LogManager.getLogger(SongCacheManager.class);
    private final ConcurrentMap<Long, EntityWrapper<T>> entityCacheMap = new ConcurrentHashMap<>();
    private final int SCHEDULE = 1000 * 60;
    private final int BASE_REMOVE_SCHEDULE = 1000 * 60;

    private void addEntity(EntityWrapper<T> entity) {
        new Thread(() -> {
            if (entityCacheMap.containsKey(entity.getEntity().getId())) {
                entityCacheMap.get(entity.getEntity().getId()).setEntity(entity.getEntity());
            } else {
                entityCacheMap.put(entity.getEntity().getId(), entity);
                logger.info("Entity cached for {} seconds", (entity.getRemoveTime() - entity.getTimeStamp()) / 1000);
            }
        }).start();
    }

    public void addEntity(T entity, long removeTime) {
        this.addEntity(new EntityWrapper<>(entity, removeTime));
    }

    public void addEntity(T entity) {
        this.addEntity(new EntityWrapper<>(entity, BASE_REMOVE_SCHEDULE));
    }

    public void removeEntity(Long id) {
        new Thread(() -> entityCacheMap.remove(id)).start();
    }

    public Optional<T> getEntity(Long id) {
        return entityCacheMap.containsKey(id)
                ? Optional.of(entityCacheMap.get(id).getEntity())
                : Optional.empty();
    }

    @Scheduled(fixedRate = SCHEDULE)
    public void unCache() {
        new Thread(() -> {
            logger.info("Cache size before emptying: {}", entityCacheMap.size());
            long currentTime = System.currentTimeMillis();
            entityCacheMap.entrySet().removeIf(e -> currentTime > e.getValue().getRemoveTime());
            logger.info("Cache size after emptying: {}", entityCacheMap.size());
        }).start();
    }

    @Data
    static class EntityWrapper<T extends CustomEntityInterface> {
        private static final Logger logger = LogManager.getLogger(SongCacheManager.class);
        private T entity;
        private long timeStamp;
        private long removeTime;

        public EntityWrapper(T entity, long removeTime) {
            this.entity = entity;
            timeStamp = System.currentTimeMillis();
            this.removeTime = timeStamp + removeTime;
        }
    }
}


