package com.school.config.application.mapper;

import com.school.config.domain.model.ConfigCategory;
import com.school.config.domain.model.Configuration;
import com.school.config.domain.model.DataType;
import com.school.config.presentation.dto.ConfigurationRequestDTO;
import com.school.config.presentation.dto.ConfigurationResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * MapStruct mapper for Configuration DTOs.
 * Handles conversion between DTOs and domain models.
 *
 * @author Backend Developer Agent
 * @version 1.0.0
 * @since 2026-02-04
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ConfigurationDTOMapper {

    /**
     * Converts ConfigurationRequestDTO to Configuration domain model.
     *
     * @param dto request DTO
     * @return domain model
     */
    default Configuration toDomain(ConfigurationRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        return Configuration.create(
            ConfigCategory.valueOf(dto.category()),
            dto.key(),
            dto.value(),
            dto.description(),
            DataType.valueOf(dto.dataType()),
            dto.isEncrypted()
        );
    }

    /**
     * Converts Configuration domain model to ConfigurationResponseDTO.
     *
     * @param configuration domain model
     * @return response DTO
     */
    default ConfigurationResponseDTO toResponseDTO(Configuration configuration) {
        if (configuration == null) {
            return null;
        }

        return new ConfigurationResponseDTO(
            configuration.getId(),
            configuration.getCategoryName(),
            configuration.getKey(),
            configuration.getValue(),
            configuration.getDescription(),
            configuration.getDataTypeName(),
            configuration.isEncrypted(),
            configuration.getVersion(),
            configuration.getCreatedAt(),
            configuration.getUpdatedAt(),
            configuration.getCreatedBy(),
            configuration.getUpdatedBy()
        );
    }
}
