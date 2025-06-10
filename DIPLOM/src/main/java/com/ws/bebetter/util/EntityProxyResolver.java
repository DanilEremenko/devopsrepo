package com.ws.bebetter.util;

import com.ws.bebetter.exception.NotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EntityProxyResolver {

    @PersistenceContext
    private EntityManager entityManager;

    public <T> T getReference(Class<T> entityClass, UUID id) {
        T entity = entityManager.getReference(entityClass, id);

        if (entity == null) {
            throw new NotFoundException("Сущность с идентификатором %s не найдена".formatted(id));
        }

        return entity;
    }

}
