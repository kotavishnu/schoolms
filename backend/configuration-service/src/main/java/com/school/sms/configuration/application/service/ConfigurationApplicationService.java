package com.school.sms.configuration.application.service;

import com.school.sms.configuration.application.dto.request.CreateConfigurationRequest;
import com.school.sms.configuration.application.dto.response.ConfigurationResponse;
import com.school.sms.configuration.application.dto.response.GroupedConfigurationResponse;
import com.school.sms.configuration.application.mapper.ConfigurationDtoMapper;
import com.school.sms.configuration.domain.exception.ConfigurationNotFoundException;
import com.school.sms.configuration.domain.model.Category;
import com.school.sms.configuration.domain.model.ConfigurationSetting;
import com.school.sms.configuration.domain.repository.ConfigurationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Application service for configuration-related use cases.
 *
 * <p>This service orchestrates business operations for configuration management,
 * including UPSERT operations (create or update), retrieval, and deletion.</p>
 *
 * <p>Key Features:</p>
 * <ul>
 *   <li>UPSERT logic - createOrUpdate will insert or update based on existence</li>
 *   <li>Category-based grouping for easy frontend consumption</li>
 *   <li>Transaction management for data consistency</li>
 * </ul>
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ConfigurationApplicationService {

    private final ConfigurationRepository configurationRepository;
    private final ConfigurationDtoMapper dtoMapper;

    /**
     * Creates or updates a configuration setting (UPSERT operation).
     *
     * <p>If a configuration with the same category and key exists, it will be updated.
     * Otherwise, a new configuration will be created.</p>
     *
     * @param category the configuration category
     * @param key the configuration key
     * @param request the create/update request
     * @return the saved configuration response
     */
    public ConfigurationResponse createOrUpdate(String category, String key, CreateConfigurationRequest request) {
        log.info("Creating or updating configuration: category={}, key={}", category, key);

        Category categoryEnum;
        try {
            categoryEnum = Category.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Invalid category: {}", category);
            throw new IllegalArgumentException("Invalid category: " + category +
                ". Valid values are: GENERAL, ACADEMIC, FINANCIAL");
        }

        // Convert request to domain model
        ConfigurationSetting configuration = dtoMapper.toDomain(request, categoryEnum, key);

        // Save (UPSERT logic is handled in repository implementation)
        ConfigurationSetting savedConfiguration = configurationRepository.save(configuration);

        log.info("Configuration saved successfully: category={}, key={}", category, key);
        return dtoMapper.toResponse(savedConfiguration);
    }

    /**
     * Retrieves a specific configuration by category and key.
     *
     * @param category the configuration category
     * @param key the configuration key
     * @return the configuration response
     * @throws ConfigurationNotFoundException if configuration not found
     */
    @Transactional(readOnly = true)
    public ConfigurationResponse getConfiguration(String category, String key) {
        log.debug("Retrieving configuration: category={}, key={}", category, key);

        Category categoryEnum;
        try {
            categoryEnum = Category.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Invalid category: {}", category);
            throw new IllegalArgumentException("Invalid category: " + category);
        }

        ConfigurationSetting configuration = configurationRepository.findByCategoryAndKey(categoryEnum, key)
            .orElseThrow(() -> new ConfigurationNotFoundException(category, key));

        return dtoMapper.toResponse(configuration);
    }

    /**
     * Retrieves all configurations, optionally filtered by category.
     *
     * @param category the optional category filter (null to get all)
     * @return the list of configuration responses
     */
    @Transactional(readOnly = true)
    public List<ConfigurationResponse> getAllConfigurations(String category) {
        log.debug("Retrieving all configurations with category filter: {}", category);

        List<ConfigurationSetting> configurations;

        if (category != null && !category.isBlank()) {
            Category categoryEnum;
            try {
                categoryEnum = Category.valueOf(category.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.error("Invalid category: {}", category);
                throw new IllegalArgumentException("Invalid category: " + category);
            }
            configurations = configurationRepository.findByCategory(categoryEnum);
        } else {
            configurations = configurationRepository.findAll();
        }

        log.debug("Found {} configurations", configurations.size());
        return dtoMapper.toResponseList(configurations);
    }

    /**
     * Retrieves configurations grouped by category as a simple key-value map.
     *
     * <p>This is useful for frontend applications that need a simple
     * Map<String, String> representation of configurations.</p>
     *
     * @param category the configuration category
     * @return the grouped configuration response
     */
    @Transactional(readOnly = true)
    public GroupedConfigurationResponse getGroupedByCategory(String category) {
        log.debug("Retrieving grouped configurations for category: {}", category);

        Category categoryEnum;
        try {
            categoryEnum = Category.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Invalid category: {}", category);
            throw new IllegalArgumentException("Invalid category: " + category);
        }

        List<ConfigurationSetting> configurations = configurationRepository.findByCategory(categoryEnum);

        log.debug("Found {} configurations in category {}", configurations.size(), category);
        return dtoMapper.toGroupedResponse(category, configurations);
    }

    /**
     * Deletes a configuration setting.
     *
     * @param category the configuration category
     * @param key the configuration key
     * @throws ConfigurationNotFoundException if configuration not found
     */
    public void deleteConfiguration(String category, String key) {
        log.info("Deleting configuration: category={}, key={}", category, key);

        Category categoryEnum;
        try {
            categoryEnum = Category.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Invalid category: {}", category);
            throw new IllegalArgumentException("Invalid category: " + category);
        }

        // Check if configuration exists before deleting
        if (!configurationRepository.existsByCategoryAndKey(categoryEnum, key)) {
            log.warn("Attempt to delete non-existent configuration: category={}, key={}", category, key);
            throw new ConfigurationNotFoundException(category, key);
        }

        configurationRepository.deleteByCategoryAndKey(categoryEnum, key);
        log.info("Configuration deleted successfully: category={}, key={}", category, key);
    }
}
