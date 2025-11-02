package com.school.management.mapper;

import com.school.management.dto.request.FeeMasterRequestDTO;
import com.school.management.dto.response.FeeMasterResponseDTO;
import com.school.management.model.FeeMaster;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for FeeMaster entity and DTOs.
 *
 * Automatically generates implementation for entity-DTO conversions.
 *
 * @author School Management Team
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FeeMasterMapper {

    /**
     * Convert FeeMaster entity to response DTO
     * @param feeMaster FeeMaster entity
     * @return FeeMasterResponseDTO
     */
    @Mapping(target = "feeTypeName", expression = "java(feeMaster.getFeeTypeName())")
    @Mapping(target = "isCurrentlyApplicable", expression = "java(feeMaster.isCurrentlyApplicable())")
    FeeMasterResponseDTO toResponseDTO(FeeMaster feeMaster);

    /**
     * Convert list of FeeMaster entities to list of response DTOs
     * @param feeMasters List of fee masters
     * @return List of FeeMasterResponseDTOs
     */
    List<FeeMasterResponseDTO> toResponseDTOList(List<FeeMaster> feeMasters);

    /**
     * Convert request DTO to FeeMaster entity
     * @param dto FeeMasterRequestDTO
     * @return FeeMaster entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    FeeMaster toEntity(FeeMasterRequestDTO dto);

    /**
     * Update existing entity from request DTO
     * @param dto FeeMasterRequestDTO
     * @param feeMaster Existing FeeMaster entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(FeeMasterRequestDTO dto, @MappingTarget FeeMaster feeMaster);
}
