package com.musicUpload.cronJobs;

import com.musicUpload.dataHandler.models.CustomEntityInterface;
import lombok.Data;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class EntityManager<T extends CustomEntityInterface> {
    private final LoadBalancerWrapper<T> loadBalancerWrapper = new LoadBalancerWrapper<>();
    private final int SCHEDULE = 15 * 1000 * 60;
    private final int REMOVE_SCHEDULE = 30 * 1000 * 60;

    public void addEntity(T entity) {
        loadBalancerWrapper.put(entity);
    }

    public void removeEntity(Long id) {
        loadBalancerWrapper.remove(id);
    }

    public Optional<T> getEntity(Long id) {
        return loadBalancerWrapper.containsKey(id) ? Optional.of(loadBalancerWrapper.get(id).getEntity())
                : Optional.empty();
    }

    @Scheduled(fixedRate = SCHEDULE)
    public void unCache() {
        long currentTime = System.currentTimeMillis();
        loadBalancerWrapper.entrySet().removeIf(e -> currentTime - e.getValue().getTimeStamp() > REMOVE_SCHEDULE);
    }

    @Data
    static class LoadBalancerWrapper<T extends CustomEntityInterface> {
        private final ConcurrentMap<Long, EntityWrapper<T>> entityMap = new ConcurrentHashMap<>();

        public boolean containsKey(Long id) {
            return entityMap.containsKey(id);
        }

        public EntityWrapper<T> get(Long id) {
            return entityMap.get(id);
        }

        public void remove(Long id) {
            entityMap.remove(id);
        }

        public Set<Map.Entry<Long, EntityWrapper<T>>> entrySet() {
            return entityMap.entrySet();
        }

        public void put(T entity) {
            entityMap.put(entity.getId(), new EntityWrapper<>(entity));
        }

        @Data
        static class EntityWrapper<T> {
            private T entity;
            private long timeStamp;

            public EntityWrapper(T entity) {
                this.entity = entity;
                timeStamp = System.currentTimeMillis();
            }
        }
    }
}
