package com.sms.student.config.application.mapper;

import com.sms.student.config.persistence.entity.ConfigurationSettingEntity;
import com.sms.student.config.presentation.dto.ConfigurationSettingDTO;
import com.sms.student.config.presentation.dto.ConfigurationSettingRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for ConfigurationSetting entity and DTOs.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConfigurationSettingMapper {

    /**
     * Convert ConfigurationSettingEntity to ConfigurationSettingDTO.
     */
    ConfigurationSettingDTO entityToDTO(ConfigurationSettingEntity entity);

    /**
     * Convert ConfigurationSettingRequest to ConfigurationSettingEntity.
     */
    ConfigurationSettingEntity requestToEntity(ConfigurationSettingRequest request);
}
