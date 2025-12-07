package com.school.sms.configuration.application.mapper;

import com.school.sms.configuration.application.dto.request.CreateConfigurationRequest;
import com.school.sms.configuration.application.dto.response.ConfigurationResponse;
import com.school.sms.configuration.application.dto.response.GroupedConfigurationResponse;
import com.school.sms.configuration.domain.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * MapStruct mapper for converting between ConfigurationSetting domain entity and DTOs.
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
@Mapper(componentModel = "spring")
public interface ConfigurationDtoMapper {

    /**
     * Converts CreateConfigurationRequest to domain ConfigurationSetting.
     *
     * @param request the create configuration request
     * @param category the configuration category
     * @param key the configuration key
     * @return the domain configuration setting
     */
    default ConfigurationSetting toDomain(CreateConfigurationRequest request, Category category, String key) {
        return ConfigurationSetting.createNew(
            category,
            ConfigurationKey.of(key),
            ConfigurationValue.of(request.getConfigValue()),
            DataType.valueOf(request.getDataType().toUpperCase()),
            request.getDescription(),
            request.getIsEncrypted() != null ? request.getIsEncrypted() : false,
            request.getUpdatedBy()
        );
    }

    /**
     * Converts domain ConfigurationSetting to ConfigurationResponse DTO.
     *
     * @param configuration the domain configuration setting
     * @return the response DTO
     */
    @Mapping(target = "category", expression = "java(configuration.getCategoryName())")
    @Mapping(target = "configKey", expression = "java(configuration.getKeyAsString())")
    @Mapping(target = "configValue", expression = "java(configuration.getValueAsString())")
    @Mapping(target = "dataType", expression = "java(configuration.getDataTypeName())")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "isEncrypted", source = "encrypted")
    @Mapping(target = "version", source = "version")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "updatedBy", source = "updatedBy")
    @Mapping(target = "settingId", source = "settingId")
    ConfigurationResponse toResponse(ConfigurationSetting configuration);

    /**
     * Converts a list of domain configurations to response DTOs.
     *
     * @param configurations the list of domain configurations
     * @return the list of response DTOs
     */
    default List<ConfigurationResponse> toResponseList(List<ConfigurationSetting> configurations) {
        return configurations.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    /**
     * Converts a list of domain configurations to a grouped response.
     *
     * <p>This creates a simple key-value map from the configurations
     * in a specific category.</p>
     *
     * @param category the category name
     * @param configurations the list of domain configurations
     * @return the grouped response DTO
     */
    default GroupedConfigurationResponse toGroupedResponse(String category, List<ConfigurationSetting> configurations) {
        Map<String, String> configMap = configurations.stream()
            .collect(Collectors.toMap(
                ConfigurationSetting::getKeyAsString,
                ConfigurationSetting::getValueAsString
            ));

        return new GroupedConfigurationResponse(category, configMap);
    }
}
