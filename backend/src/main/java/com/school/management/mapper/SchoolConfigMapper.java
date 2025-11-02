package com.school.management.mapper;

import com.school.management.dto.request.SchoolConfigRequestDTO;
import com.school.management.dto.response.SchoolConfigResponseDTO;
import com.school.management.model.SchoolConfig;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for SchoolConfig entity and DTOs.
 *
 * Automatically generates implementation for entity-DTO conversions.
 *
 * @author School Management Team
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SchoolConfigMapper {

    /**
     * Convert SchoolConfig entity to response DTO
     * @param config SchoolConfig entity
     * @return SchoolConfigResponseDTO
     */
    @Mapping(target = "intValue", expression = "java(config.getIntValue())")
    @Mapping(target = "booleanValue", expression = "java(config.getBooleanValue())")
    @Mapping(target = "isSystemConfig", expression = "java(config.isSystemConfig())")
    SchoolConfigResponseDTO toResponseDTO(SchoolConfig config);

    /**
     * Convert list of SchoolConfig entities to list of response DTOs
     * @param configs List of configurations
     * @return List of SchoolConfigResponseDTOs
     */
    List<SchoolConfigResponseDTO> toResponseDTOList(List<SchoolConfig> configs);

    /**
     * Convert request DTO to SchoolConfig entity
     * @param dto SchoolConfigRequestDTO
     * @return SchoolConfig entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    SchoolConfig toEntity(SchoolConfigRequestDTO dto);

    /**
     * Update existing entity from request DTO
     * @param dto SchoolConfigRequestDTO
     * @param config Existing SchoolConfig entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(SchoolConfigRequestDTO dto, @MappingTarget SchoolConfig config);
}
