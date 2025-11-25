package com.school.sms.configuration.application.service;

import com.school.sms.common.exception.DuplicateResourceException;
import com.school.sms.common.exception.ResourceNotFoundException;
import com.school.sms.configuration.application.dto.request.CreateConfigRequest;
import com.school.sms.configuration.application.dto.request.UpdateConfigRequest;
import com.school.sms.configuration.application.dto.response.ConfigurationPageResponse;
import com.school.sms.configuration.application.dto.response.ConfigurationResponse;
import com.school.sms.configuration.application.mapper.ConfigurationMapper;
import com.school.sms.configuration.domain.model.Configuration;
import com.school.sms.configuration.domain.model.ConfigurationCategory;
import com.school.sms.configuration.domain.repository.ConfigurationRepository;
import com.school.sms.configuration.infrastructure.cache.ConfigurationCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Configuration service implementation
 * Orchestrates domain logic, validation, and caching
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConfigurationServiceImpl implements ConfigurationService {

    private final ConfigurationRepository configurationRepository;
    private final ConfigurationMapper configurationMapper;
    private final ConfigurationCacheService cacheService;

    @Override
    @Transactional
    public ConfigurationResponse createConfiguration(CreateConfigRequest request) {
        log.info("Creating configuration: category={}, key={}", request.getCategory(), request.getConfigKey());

        ConfigurationCategory category = ConfigurationCategory.valueOf(request.getCategory());

        // Check uniqueness constraint
        if (configurationRepository.existsByCategoryAndConfigKey(category, request.getConfigKey())) {
            throw new DuplicateResourceException(
                    "Configuration with category " + category + " and key " + request.getConfigKey() + " already exists"
            );
        }

        // Create domain entity
        Configuration configuration = Configuration.create(
                category,
                request.getConfigKey(),
                request.getConfigValue(),
                request.getDescription(),
                "system" // TODO: Get from SecurityContext in Phase 2
        );

        // Persist
        Configuration savedConfiguration = configurationRepository.save(configuration);

        // Invalidate category cache
        cacheService.evictByCategory(category.name());

        log.info("Configuration created successfully: id={}", savedConfiguration.getId());

        return configurationMapper.toResponse(savedConfiguration);
    }

    @Override
    public ConfigurationResponse getConfigurationById(Long id) {
        log.debug("Fetching configuration by ID: {}", id);

        Configuration configuration = configurationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Configuration with ID " + id + " not found"));

        return configurationMapper.toResponse(configuration);
    }

    @Override
    public List<ConfigurationResponse> getConfigurationsByCategory(String categoryName) {
        log.debug("Fetching configurations by category: {}", categoryName);

        // Check cache first
        List<Configuration> cachedConfigs = cacheService.getByCategory(categoryName);
        if (cachedConfigs != null) {
            log.debug("Cache hit for category: {}", categoryName);
            return cachedConfigs.stream()
                    .map(configurationMapper::toResponse)
                    .toList();
        }

        // Cache miss - fetch from database
        ConfigurationCategory category = ConfigurationCategory.valueOf(categoryName);
        List<Configuration> configurations = configurationRepository.findByCategory(category);

        // Cache for future requests
        cacheService.cacheByCategory(categoryName, configurations);

        return configurations.stream()
                .map(configurationMapper::toResponse)
                .toList();
    }

    @Override
    public ConfigurationPageResponse getAllConfigurations(Pageable pageable) {
        log.debug("Fetching all configurations with pagination: {}", pageable);

        Page<Configuration> configurationPage = configurationRepository.findAll(pageable);

        return configurationMapper.toPageResponse(configurationPage);
    }

    @Override
    @Transactional
    public ConfigurationResponse updateConfiguration(Long id, UpdateConfigRequest request) {
        log.info("Updating configuration ID: {}", id);

        Configuration configuration = configurationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Configuration with ID " + id + " not found"));

        // Update via domain method
        configuration.update(
                request.getConfigValue(),
                request.getDescription(),
                "system" // TODO: Get from SecurityContext
        );

        Configuration updatedConfiguration = configurationRepository.save(configuration);

        // Invalidate cache
        cacheService.evictByCategory(updatedConfiguration.getCategory().name());

        log.info("Configuration updated successfully: id={}", id);

        return configurationMapper.toResponse(updatedConfiguration);
    }

    @Override
    @Transactional
    public void deleteConfiguration(Long id) {
        log.info("Deleting configuration ID: {}", id);

        Configuration configuration = configurationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Configuration with ID " + id + " not found"));

        configurationRepository.deleteById(id);

        // Invalidate cache
        cacheService.evictByCategory(configuration.getCategory().name());

        log.info("Configuration deleted: id={}", id);
    }
}
