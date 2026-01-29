package com.school.configuration.service;

import com.school.configuration.controller.dto.request.ConfigurationRequest;
import com.school.configuration.controller.dto.response.ConfigurationResponse;
import com.school.configuration.domain.exception.ConfigurationNotFoundException;
import com.school.configuration.domain.model.ConfigCategory;
import com.school.configuration.domain.model.Configuration;
import com.school.configuration.domain.repository.ConfigurationRepository;
import com.school.configuration.service.mapper.ConfigurationDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service layer for configuration management with caching support.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConfigurationService {

    private final ConfigurationRepository configurationRepository;
    private final ConfigurationDtoMapper dtoMapper;

    /**
     * Get all configurations, optionally filtered by category.
     * Cached to reduce database queries for frequently accessed configurations.
     *
     * @param category Optional category filter (null returns all)
     * @return List of configuration responses
     */
    @Cacheable(value = "configurationsByCategory", key = "#category != null ? #category.name() : 'ALL'")
    public List<ConfigurationResponse> getAllConfigurations(ConfigCategory category) {
        log.debug("Fetching all configurations for category: {}", category);

        List<Configuration> configurations = (category != null)
                ? configurationRepository.findByCategory(category)
                : configurationRepository.findAll();

        log.debug("Found {} configurations", configurations.size());
        return dtoMapper.toResponseList(configurations);
    }

    /**
     * Get a single configuration by category and key.
     * Cached with a composite key for fast lookup.
     *
     * @param category Configuration category
     * @param key Configuration key
     * @return Configuration response
     * @throws ConfigurationNotFoundException if not found
     */
    @Cacheable(value = "configurations", key = "#category.name() + ':' + #key")
    public ConfigurationResponse getConfiguration(ConfigCategory category, String key) {
        log.debug("Fetching configuration: category={}, key={}", category, key);

        Configuration configuration = configurationRepository.findByCategoryAndKey(category, key)
                .orElseThrow(() -> new ConfigurationNotFoundException(category, key));

        return dtoMapper.toResponse(configuration);
    }

    /**
     * Create or update a configuration (upsert operation).
     * Returns HTTP 201 if created, 200 if updated.
     * Evicts all related caches on modification.
     *
     * @param category Configuration category
     * @param key Configuration key
     * @param request Configuration request data
     * @return Configuration response and a boolean indicating if it was created (true) or updated (false)
     */
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "configurations", key = "#category.name() + ':' + #key"),
            @CacheEvict(value = "configurationsByCategory", allEntries = true),
            @CacheEvict(value = "groupedConfigurations", allEntries = true)
    })
    public UpsertResult upsertConfiguration(ConfigCategory category, String key, ConfigurationRequest request) {
        log.debug("Upserting configuration: category={}, key={}", category, key);

        // Check if configuration exists
        boolean exists = configurationRepository.existsByCategoryAndKey(category, key);

        Configuration configuration;
        if (exists) {
            // Update existing configuration
            log.debug("Updating existing configuration: category={}, key={}", category, key);
            configuration = configurationRepository.findByCategoryAndKey(category, key)
                    .orElseThrow(() -> new ConfigurationNotFoundException(category, key));

            // Update fields
            configuration = Configuration.builder()
                    .id(configuration.getId())
                    .category(category)
                    .key(key)
                    .value(request.getValue())
                    .description(request.getDescription())
                    .dataType(request.getDataType())
                    .isEncrypted(request.getIsEncrypted())
                    .version(configuration.getVersion()) // Preserve version for optimistic locking
                    .updatedAt(LocalDateTime.now())
                    .build();
        } else {
            // Create new configuration
            log.debug("Creating new configuration: category={}, key={}", category, key);
            configuration = Configuration.builder()
                    .category(category)
                    .key(key)
                    .value(request.getValue())
                    .description(request.getDescription())
                    .dataType(request.getDataType())
                    .isEncrypted(request.getIsEncrypted())
                    .version(0L) // Initial version
                    .updatedAt(LocalDateTime.now())
                    .build();
        }

        Configuration saved = configurationRepository.save(configuration);
        log.info("Configuration upserted successfully: id={}, category={}, key={}, isNew={}",
                saved.getId(), category, key, !exists);

        return new UpsertResult(dtoMapper.toResponse(saved), !exists);
    }

    /**
     * Delete a configuration by category and key.
     * Evicts all related caches.
     *
     * @param category Configuration category
     * @param key Configuration key
     * @throws ConfigurationNotFoundException if not found
     */
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "configurations", key = "#category.name() + ':' + #key"),
            @CacheEvict(value = "configurationsByCategory", allEntries = true),
            @CacheEvict(value = "groupedConfigurations", allEntries = true)
    })
    public void deleteConfiguration(ConfigCategory category, String key) {
        log.debug("Deleting configuration: category={}, key={}", category, key);

        Configuration configuration = configurationRepository.findByCategoryAndKey(category, key)
                .orElseThrow(() -> new ConfigurationNotFoundException(category, key));

        configurationRepository.delete(configuration);
        log.info("Configuration deleted successfully: category={}, key={}", category, key);
    }

    /**
     * Get configurations grouped as a key-value map.
     * Useful for frontend applications that need all configurations in a flat structure.
     * Cached with category-specific key.
     *
     * @param category Configuration category
     * @return Map of configuration keys to values
     */
    @Cacheable(value = "groupedConfigurations", key = "#category.name()")
    public Map<String, String> getGroupedConfigurations(ConfigCategory category) {
        log.debug("Fetching grouped configurations for category: {}", category);

        List<Configuration> configurations = configurationRepository.findByCategory(category);

        Map<String, String> grouped = configurations.stream()
                .collect(Collectors.toMap(
                        Configuration::getKey,
                        Configuration::getValue
                ));

        log.debug("Grouped {} configurations for category: {}", grouped.size(), category);
        return grouped;
    }

    /**
     * Result of an upsert operation.
     */
    public record UpsertResult(ConfigurationResponse response, boolean isCreated) {}
}
