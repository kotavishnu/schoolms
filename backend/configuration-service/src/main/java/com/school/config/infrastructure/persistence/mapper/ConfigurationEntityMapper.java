package com.school.config.infrastructure.persistence.mapper;

import com.school.config.domain.model.Configuration;
import com.school.config.infrastructure.persistence.entity.ConfigurationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * MapStruct mapper for Configuration domain model and ConfigurationEntity.
 * Handles conversion between domain and persistence layers.
 *
 * @author Backend Developer Agent
 * @version 1.0.0
 * @since 2026-02-04
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ConfigurationEntityMapper {

    /**
     * Converts ConfigurationEntity to Configuration domain model.
     *
     * @param entity JPA entity
     * @return domain model
     */
    default Configuration toDomain(ConfigurationEntity entity) {
        if (entity == null) {
            return null;
        }

        Configuration config = Configuration.create(
            entity.getCategory(),
            entity.getKey(),
            entity.getValue(),
            entity.getDescription(),
            entity.getDataType(),
            entity.isEncrypted()
        );

        config.setId(entity.getId());
        config.setVersion(entity.getVersion());
        config.setCreatedAt(entity.getCreatedAt());
        config.setUpdatedAt(entity.getUpdatedAt());
        config.setCreatedBy(entity.getCreatedBy());
        config.setUpdatedBy(entity.getUpdatedBy());

        return config;
    }

    /**
     * Converts Configuration domain model to ConfigurationEntity.
     *
     * @param domain domain model
     * @return JPA entity
     */
    default ConfigurationEntity toEntity(Configuration domain) {
        if (domain == null) {
            return null;
        }

        return ConfigurationEntity.builder()
            .id(domain.getId())
            .category(domain.getCategory())
            .key(domain.getKey())
            .value(domain.getValue())
            .description(domain.getDescription())
            .dataType(domain.getDataType())
            .isEncrypted(domain.isEncrypted())
            .version(domain.getVersion())
            .createdAt(domain.getCreatedAt())
            .updatedAt(domain.getUpdatedAt())
            .createdBy(domain.getCreatedBy())
            .updatedBy(domain.getUpdatedBy())
            .build();
    }
}
