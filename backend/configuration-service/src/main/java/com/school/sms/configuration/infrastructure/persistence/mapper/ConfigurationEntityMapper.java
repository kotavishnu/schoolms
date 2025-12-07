package com.school.sms.configuration.infrastructure.persistence.mapper;

import com.school.sms.configuration.domain.model.*;
import com.school.sms.configuration.infrastructure.persistence.entity.ConfigurationJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * MapStruct mapper for converting between ConfigurationSetting domain entity and ConfigurationJpaEntity.
 *
 * <p>This mapper handles the conversion between the domain model and the JPA persistence layer,
 * including value object conversions and enum mappings.</p>
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
@Mapper(componentModel = "spring")
public interface ConfigurationEntityMapper {

    /**
     * Converts a domain ConfigurationSetting to a JPA entity.
     *
     * @param configuration the domain configuration setting
     * @return the JPA entity
     */
    @Mapping(target = "settingId", source = "settingId")
    @Mapping(target = "category", source = "category", qualifiedByName = "categoryToEnum")
    @Mapping(target = "configKey", source = "configKey", qualifiedByName = "configKeyToString")
    @Mapping(target = "configValue", source = "configValue", qualifiedByName = "configValueToString")
    @Mapping(target = "dataType", source = "dataType", qualifiedByName = "dataTypeToEnum")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "isEncrypted", source = "encrypted")
    @Mapping(target = "version", source = "version")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "updatedBy", source = "updatedBy")
    ConfigurationJpaEntity toJpaEntity(ConfigurationSetting configuration);

    /**
     * Converts a JPA entity to a domain ConfigurationSetting.
     *
     * @param entity the JPA entity
     * @return the domain configuration setting
     */
    default ConfigurationSetting toDomain(ConfigurationJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return ConfigurationSetting.fromRepository(
            entity.getSettingId(),
            enumToCategory(entity.getCategory()),
            ConfigurationKey.of(entity.getConfigKey()),
            ConfigurationValue.of(entity.getConfigValue()),
            enumToDataType(entity.getDataType()),
            entity.getDescription(),
            entity.getIsEncrypted() != null ? entity.getIsEncrypted() : false,
            entity.getVersion(),
            entity.getUpdatedAt(),
            entity.getUpdatedBy()
        );
    }

    /**
     * Converts domain Category to JPA enum.
     */
    @Named("categoryToEnum")
    default ConfigurationJpaEntity.CategoryEnum categoryToEnum(Category category) {
        return category != null ? ConfigurationJpaEntity.CategoryEnum.valueOf(category.name()) : null;
    }

    /**
     * Converts JPA enum to domain Category.
     */
    @Named("enumToCategory")
    default Category enumToCategory(ConfigurationJpaEntity.CategoryEnum category) {
        return category != null ? Category.valueOf(category.name()) : null;
    }

    /**
     * Converts domain DataType to JPA enum.
     */
    @Named("dataTypeToEnum")
    default ConfigurationJpaEntity.DataTypeEnum dataTypeToEnum(DataType dataType) {
        return dataType != null ? ConfigurationJpaEntity.DataTypeEnum.valueOf(dataType.name()) : null;
    }

    /**
     * Converts JPA enum to domain DataType.
     */
    @Named("enumToDataType")
    default DataType enumToDataType(ConfigurationJpaEntity.DataTypeEnum dataType) {
        return dataType != null ? DataType.valueOf(dataType.name()) : null;
    }

    /**
     * Converts ConfigurationKey value object to String.
     */
    @Named("configKeyToString")
    default String configKeyToString(ConfigurationKey configKey) {
        return configKey != null ? configKey.getValue() : null;
    }

    /**
     * Converts ConfigurationValue value object to String.
     */
    @Named("configValueToString")
    default String configValueToString(ConfigurationValue configValue) {
        return configValue != null ? configValue.getValue() : null;
    }
}
