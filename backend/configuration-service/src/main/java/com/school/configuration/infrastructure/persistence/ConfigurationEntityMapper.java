package com.school.configuration.infrastructure.persistence;

import com.school.configuration.domain.model.ConfigCategory;
import com.school.configuration.domain.model.Configuration;
import com.school.configuration.domain.model.DataType;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for converting between Configuration domain model and ConfigurationEntity JPA entity.
 * Handles enum conversions between domain enums and database strings.
 * Keeps domain layer clean from JPA annotations.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConfigurationEntityMapper {

    /**
     * Converts domain Configuration to JPA ConfigurationEntity.
     * Converts enum types to strings for database storage.
     *
     * @param domain the domain configuration object
     * @return the JPA entity
     */
    @Mapping(target = "category", source = "category", qualifiedByName = "categoryToString")
    @Mapping(target = "dataType", source = "dataType", qualifiedByName = "dataTypeToString")
    ConfigurationEntity toEntity(Configuration domain);

    /**
     * Converts JPA ConfigurationEntity to domain Configuration.
     * Converts string category and dataType to enum types.
     *
     * @param entity the JPA entity
     * @return the domain configuration object
     */
    @Mapping(target = "category", source = "category", qualifiedByName = "stringToCategory")
    @Mapping(target = "dataType", source = "dataType", qualifiedByName = "stringToDataType")
    Configuration toDomain(ConfigurationEntity entity);

    /**
     * Converts list of JPA entities to list of domain objects.
     *
     * @param entities list of JPA entities
     * @return list of domain configuration objects
     */
    List<Configuration> toDomainList(List<ConfigurationEntity> entities);

    /**
     * Converts ConfigCategory enum to string.
     */
    @Named("categoryToString")
    default String categoryToString(ConfigCategory category) {
        return category != null ? category.name() : null;
    }

    /**
     * Converts string to ConfigCategory enum.
     */
    @Named("stringToCategory")
    default ConfigCategory stringToCategory(String category) {
        return category != null ? ConfigCategory.valueOf(category) : null;
    }

    /**
     * Converts DataType enum to string.
     */
    @Named("dataTypeToString")
    default String dataTypeToString(DataType dataType) {
        return dataType != null ? dataType.name() : null;
    }

    /**
     * Converts string to DataType enum.
     */
    @Named("stringToDataType")
    default DataType stringToDataType(String dataType) {
        return dataType != null ? DataType.valueOf(dataType) : null;
    }
}
