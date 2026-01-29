package com.school.configuration.service.mapper;

import com.school.configuration.controller.dto.request.ConfigurationRequest;
import com.school.configuration.controller.dto.response.ConfigurationResponse;
import com.school.configuration.domain.model.Configuration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * MapStruct mapper for converting between Configuration domain models and DTOs.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConfigurationDtoMapper {

    /**
     * Convert ConfigurationRequest to Configuration domain model.
     * Note: id, category, key, version, and updatedAt are set by the service layer
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "key", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Configuration toDomain(ConfigurationRequest request);

    /**
     * Convert Configuration domain model to ConfigurationResponse DTO.
     */
    ConfigurationResponse toResponse(Configuration configuration);

    /**
     * Convert list of Configuration domain models to list of ConfigurationResponse DTOs.
     */
    List<ConfigurationResponse> toResponseList(List<Configuration> configurations);
}
