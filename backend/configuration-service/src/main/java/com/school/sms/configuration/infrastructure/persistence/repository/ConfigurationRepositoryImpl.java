package com.school.sms.configuration.infrastructure.persistence.repository;

import com.school.sms.configuration.domain.model.Category;
import com.school.sms.configuration.domain.model.ConfigurationSetting;
import com.school.sms.configuration.domain.repository.ConfigurationRepository;
import com.school.sms.configuration.infrastructure.persistence.entity.ConfigurationJpaEntity;
import com.school.sms.configuration.infrastructure.persistence.mapper.ConfigurationEntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of ConfigurationRepository using JPA.
 *
 * <p>This class bridges the domain layer and infrastructure layer,
 * converting between domain models and JPA entities.</p>
 *
 * <p>The save method implements UPSERT logic - it will update an existing
 * configuration if one exists with the same category and key, otherwise
 * it will create a new one.</p>
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
@Repository
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ConfigurationRepositoryImpl implements ConfigurationRepository {

    private final ConfigurationJpaRepository jpaRepository;
    private final ConfigurationEntityMapper mapper;

    @Override
    public ConfigurationSetting save(ConfigurationSetting configuration) {
        log.debug("Saving configuration: category={}, key={}",
            configuration.getCategoryName(), configuration.getKeyAsString());

        ConfigurationJpaEntity entity;

        // UPSERT logic: Check if configuration exists
        ConfigurationJpaEntity.CategoryEnum categoryEnum =
            ConfigurationJpaEntity.CategoryEnum.valueOf(configuration.getCategoryName());

        Optional<ConfigurationJpaEntity> existingEntity =
            jpaRepository.findByCategoryAndConfigKey(categoryEnum, configuration.getKeyAsString());

        if (existingEntity.isPresent()) {
            // Update existing configuration
            entity = existingEntity.get();
            entity.setConfigValue(configuration.getValueAsString());
            entity.setDataType(ConfigurationJpaEntity.DataTypeEnum.valueOf(configuration.getDataTypeName()));
            entity.setDescription(configuration.getDescription());
            entity.setUpdatedBy(configuration.getUpdatedBy());
            // updatedAt will be set by @PreUpdate
            log.debug("Updating existing configuration");
        } else {
            // Create new configuration
            entity = mapper.toJpaEntity(configuration);
            log.debug("Creating new configuration");
        }

        ConfigurationJpaEntity savedEntity = jpaRepository.save(entity);
        ConfigurationSetting savedConfiguration = mapper.toDomain(savedEntity);

        log.info("Configuration saved successfully: category={}, key={}",
            savedConfiguration.getCategoryName(), savedConfiguration.getKeyAsString());
        return savedConfiguration;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ConfigurationSetting> findByCategoryAndKey(Category category, String key) {
        log.debug("Finding configuration by category={} and key={}", category, key);

        ConfigurationJpaEntity.CategoryEnum categoryEnum =
            ConfigurationJpaEntity.CategoryEnum.valueOf(category.name());

        return jpaRepository.findByCategoryAndConfigKey(categoryEnum, key)
            .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConfigurationSetting> findByCategory(Category category) {
        log.debug("Finding all configurations in category={}", category);

        ConfigurationJpaEntity.CategoryEnum categoryEnum =
            ConfigurationJpaEntity.CategoryEnum.valueOf(category.name());

        List<ConfigurationSetting> configurations = jpaRepository.findByCategory(categoryEnum)
            .stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());

        log.debug("Found {} configurations in category={}", configurations.size(), category);
        return configurations;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConfigurationSetting> findAll() {
        log.debug("Finding all configurations");

        List<ConfigurationSetting> configurations = jpaRepository.findAll()
            .stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());

        log.debug("Found {} total configurations", configurations.size());
        return configurations;
    }

    @Override
    public void deleteByCategoryAndKey(Category category, String key) {
        log.debug("Deleting configuration: category={}, key={}", category, key);

        ConfigurationJpaEntity.CategoryEnum categoryEnum =
            ConfigurationJpaEntity.CategoryEnum.valueOf(category.name());

        jpaRepository.deleteByCategoryAndConfigKey(categoryEnum, key);

        log.info("Configuration deleted: category={}, key={}", category, key);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCategoryAndKey(Category category, String key) {
        log.debug("Checking if configuration exists: category={}, key={}", category, key);

        ConfigurationJpaEntity.CategoryEnum categoryEnum =
            ConfigurationJpaEntity.CategoryEnum.valueOf(category.name());

        return jpaRepository.existsByCategoryAndConfigKey(categoryEnum, key);
    }
}
