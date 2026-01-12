package com.schoolms.configuration.infrastructure.persistence.adapter;

import com.schoolms.configuration.domain.model.Configuration;
import com.schoolms.configuration.infrastructure.persistence.entity.ConfigurationJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Configuration Entity Mapper (BE-032)
 * Maps between domain and JPA entity
 */
@Component
public class ConfigurationEntityMapper {

    /**
     * Convert domain model to JPA entity
     */
    public ConfigurationJpaEntity toJpaEntity(Configuration domain) {
        if (domain == null) {
            return null;
        }

        ConfigurationJpaEntity entity = new ConfigurationJpaEntity();
        entity.setId(domain.getId());
        entity.setCategory(domain.getCategory());
        entity.setKey(domain.getKey());
        entity.setValue(domain.getValue());
        entity.setDescription(domain.getDescription());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setLastUpdated(domain.getLastUpdated());
        entity.setVersion(domain.getVersion());

        return entity;
    }

    /**
     * Convert JPA entity to domain model
     */
    public Configuration toDomain(ConfigurationJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return Configuration.reconstruct(
            entity.getId(),
            entity.getCategory(),
            entity.getKey(),
            entity.getValue(),
            entity.getDescription(),
            entity.getCreatedAt(),
            entity.getLastUpdated(),
            entity.getVersion()
        );
    }
}
