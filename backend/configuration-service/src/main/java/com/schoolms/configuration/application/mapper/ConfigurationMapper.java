package com.schoolms.configuration.application.mapper;

import com.schoolms.configuration.domain.model.Configuration;
import com.schoolms.configuration.presentation.dto.ConfigurationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import java.util.List;

/**
 * Configuration Mapper (BE-035)
 * MapStruct mapper for DTOs
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ConfigurationMapper {

    ConfigurationResponse toResponse(Configuration configuration);

    List<ConfigurationResponse> toResponseList(List<Configuration> configurations);
}
