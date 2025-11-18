package com.school.sms.configuration.application.mapper;

import com.school.sms.configuration.application.dto.*;
import com.school.sms.configuration.domain.entity.ConfigurationSetting;
import com.school.sms.configuration.domain.entity.SchoolProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * MapStruct mapper for Configuration entities and DTOs.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ConfigurationMapper {

    /**
     * Map ConfigurationSetting entity to SettingResponse DTO.
     *
     * @param setting the entity
     * @return the DTO
     */
    @Mapping(target = "settingId", source = "id")
    SettingResponse toSettingResponse(ConfigurationSetting setting);

    /**
     * Map CreateSettingRequest DTO to ConfigurationSetting entity.
     *
     * @param request the DTO
     * @return the entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    ConfigurationSetting toEntity(CreateSettingRequest request);

    /**
     * Map SchoolProfile entity to SchoolProfileResponse DTO.
     *
     * @param profile the entity
     * @return the DTO
     */
    SchoolProfileResponse toSchoolProfileResponse(SchoolProfile profile);

    /**
     * Map UpdateSchoolProfileRequest DTO to SchoolProfile entity.
     *
     * @param request the DTO
     * @return the entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    SchoolProfile toEntity(UpdateSchoolProfileRequest request);
}
