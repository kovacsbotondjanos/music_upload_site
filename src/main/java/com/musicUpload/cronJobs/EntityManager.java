package com.musicUpload.cronJobs;

import com.musicUpload.dataHandler.models.CustomEntityInterface;
import lombok.Data;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class EntityManager<T extends CustomEntityInterface> {
    private final ConcurrentMap<Long, EntityWrapper<T>> entityCacheMap = new ConcurrentHashMap<>();
    private final int SCHEDULE = 10 * 1000 * 60;
    private final int REMOVE_SCHEDULE = 30 * 1000 * 60;

    public void addEntity(T entity) {
        new Thread(() -> {
            if (entityCacheMap.containsKey(entity.getId())) {
                entityCacheMap.get(entity.getId()).setEntity(entity);
            } else {
                entityCacheMap.put(entity.getId(), new EntityWrapper<>(entity));
            }
        }).start();
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
            long currentTime = System.currentTimeMillis();
            entityCacheMap.entrySet().removeIf(e -> currentTime - e.getValue().getTimeStamp() > REMOVE_SCHEDULE);
        }).start();
    }

    @Data
    static class EntityWrapper<T extends CustomEntityInterface> {
        private T entity;
        private long timeStamp;

        public EntityWrapper(T entity) {
            this.entity = entity;
            timeStamp = System.currentTimeMillis();
        }
    }
}


