package com.school.configuration.service;

import com.school.configuration.domain.entity.ConfigCategory;
import com.school.configuration.domain.entity.Configuration;
import com.school.configuration.dto.request.ConfigurationRequest;
import com.school.configuration.dto.response.ConfigurationResponse;
import com.school.configuration.exception.ConfigurationNotFoundException;
import com.school.configuration.mapper.ConfigurationMapper;
import com.school.configuration.repository.ConfigurationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Configuration Service
 *
 * Core business logic for configuration management.
 * Implements CRUD operations with caching and transaction management.
 *
 * Caching Strategy (D-003):
 * - configurations: All configurations (TTL: 4 hours)
 * - configurationsByCategory: Category-filtered configs (TTL: 4 hours)
 *
 * Annotations:
 * - @Transactional: All methods run in transactions
 * - @Cacheable/@CacheEvict: Redis caching integration (DB 1)
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ConfigurationService {

    private final ConfigurationRepository repository;
    private final ConfigurationMapper mapper;

    // ==================== READ - Get All ====================

    /**
     * Get all configurations
     *
     * Returns all configuration settings ordered by category and key.
     * Uses Redis cache with key: "configurations::all"
     * TTL: 4 hours (configured in CacheConfig)
     *
     * @return List of all configurations
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "configurations", key = "'all'")
    public List<ConfigurationResponse> getAllConfigurations() {
        log.debug("Fetching all configurations");

        List<Configuration> configurations = repository.findAllByOrderByCategoryAscKeyAsc();

        log.debug("Found {} configurations", configurations.size());
        return configurations.stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());
    }

    // ==================== READ - Get by Category ====================

    /**
     * Get configurations by category
     *
     * Returns all configuration settings in a specific category.
     * Uses Redis cache with key: "configurationsByCategory::{category}"
     * TTL: 4 hours
     *
     * @param category Configuration category
     * @return List of configurations in the category
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "configurationsByCategory", key = "#category.name()")
    public List<ConfigurationResponse> getConfigurationsByCategory(ConfigCategory category) {
        log.debug("Fetching configurations for category: {}", category);

        List<Configuration> configurations = repository.findByCategory(category);

        log.debug("Found {} configurations in category {}", configurations.size(), category);
        return configurations.stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());
    }

    // ==================== READ - Get by Category and Key ====================

    /**
     * Get configuration by category and key (composite business key)
     *
     * This is the primary lookup method for specific configurations.
     * Uses Redis cache with key: "configuration::{category}:{key}"
     * TTL: 4 hours
     *
     * @param category Configuration category
     * @param key Configuration key
     * @return Configuration response
     * @throws ConfigurationNotFoundException if not found
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "configuration", key = "#category.name() + ':' + #key")
    public ConfigurationResponse getConfiguration(ConfigCategory category, String key) {
        log.debug("Fetching configuration: {}:{}", category, key);

        return repository.findByCategoryAndKey(category, key)
            .map(mapper::toResponse)
            .orElseThrow(() -> new ConfigurationNotFoundException(category, key));
    }

    // ==================== CREATE / UPDATE (Upsert) ====================

    /**
     * Upsert configuration (create if not exists, update if exists)
     *
     * This method implements true upsert:
     * - If configuration exists: Update it
     * - If configuration does not exist: Create it
     *
     * Optimistic Locking (D-003):
     * - For updates: version field prevents concurrent modification
     * - For creates: version is auto-initialized by JPA
     *
     * Cache eviction: Clears all configuration caches to maintain consistency
     *
     * @param request Configuration data
     * @return Created or updated configuration
     */
    @CacheEvict(
        value = {"configurations", "configurationsByCategory", "configuration"},
        allEntries = true
    )
    public ConfigurationResponse upsertConfiguration(ConfigurationRequest request) {
        log.info("Upserting configuration: {}:{}", request.getCategory(), request.getKey());

        Configuration configuration = repository
            .findByCategoryAndKey(request.getCategory(), request.getKey())
            .map(existing -> {
                log.debug("Configuration exists, updating: {}:{}", request.getCategory(), request.getKey());

                // Update existing configuration
                mapper.updateEntityFromRequest(request, existing);

                // Set version for optimistic locking if provided
                if (request.getVersion() != null) {
                    existing.setVersion(request.getVersion());
                }

                return existing;
            })
            .orElseGet(() -> {
                log.debug("Configuration does not exist, creating: {}:{}", request.getCategory(), request.getKey());

                // Create new configuration
                return mapper.toEntity(request);
            });

        Configuration saved = repository.save(configuration);
        log.info("Configuration upserted successfully: {}:{}", saved.getCategory(), saved.getKey());

        return mapper.toResponse(saved);
    }

    // ==================== DELETE ====================

    /**
     * Delete configuration by category and key
     *
     * Cache eviction: Clears all configuration caches
     *
     * @param category Configuration category
     * @param key Configuration key
     * @throws ConfigurationNotFoundException if not found
     */
    @CacheEvict(
        value = {"configurations", "configurationsByCategory", "configuration"},
        allEntries = true
    )
    public void deleteConfiguration(ConfigCategory category, String key) {
        log.info("Deleting configuration: {}:{}", category, key);

        // Verify configuration exists
        if (!repository.existsByCategoryAndKey(category, key)) {
            throw new ConfigurationNotFoundException(category, key);
        }

        Long deletedCount = repository.deleteByCategoryAndKey(category, key);
        log.info("Configuration deleted successfully: {}:{} (count: {})", category, key, deletedCount);
    }

    // ==================== GROUPED CONFIGURATIONS ====================

    /**
     * Get configurations grouped by key for a specific category
     *
     * Returns a map of key → value for easy client-side lookup.
     * Useful for fetching all settings in a category as a configuration map.
     *
     * Example: {"SCHOOL_NAME": "ABC School", "SCHOOL_ADDRESS": "123 Main St"}
     *
     * @param category Configuration category
     * @return Map of key → value
     */
    @Transactional(readOnly = true)
    public Map<String, String> getGroupedConfigurations(ConfigCategory category) {
        log.debug("Fetching grouped configurations for category: {}", category);

        List<Configuration> configurations = repository.findByCategory(category);

        return configurations.stream()
            .collect(Collectors.toMap(
                Configuration::getKey,
                Configuration::getValue
            ));
    }
}
