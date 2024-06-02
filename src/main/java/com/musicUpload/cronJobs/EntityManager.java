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
    private final ConcurrentMap<Long, EntityWrapper<T>> entityMap = new ConcurrentHashMap<>();
    private final int SCHEDULE = 15 * 1000 * 60;
    private final int REMOVE_SCHEDULE = 30 * 1000 * 60;

    public void addEntity(T entity) {
        entityMap.put(entity.getId(), new EntityWrapper<>(entity));
    }

    public void removeEntity(Long id) {
        entityMap.remove(id);
    }

    public Optional<T> getEntity(Long id) {
        return entityMap.containsKey(id) ? Optional.of(entityMap.get(id).getEntity())
                : Optional.empty();
    }

    @Scheduled(fixedRate = SCHEDULE)
    public void unCache() {
        long currentTime = System.currentTimeMillis();
        //TODO: evaluate if i need to save the entity here, or elsewhere, but first make sure this actually works
        entityMap.entrySet().removeIf(e -> currentTime - e.getValue().getTimeStamp() > REMOVE_SCHEDULE);
    }
}

@Data
class EntityWrapper<T> {
    private T entity;
    private long timeStamp;

    public EntityWrapper(T entity) {
        this.entity = entity;
        timeStamp = System.currentTimeMillis();
    }
}
