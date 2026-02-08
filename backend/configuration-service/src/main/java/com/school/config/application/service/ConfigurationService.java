package com.school.config.application.service;

import com.school.config.application.mapper.ConfigurationDTOMapper;
import com.school.config.domain.model.ConfigCategory;
import com.school.config.domain.model.Configuration;
import com.school.config.domain.repository.ConfigurationRepository;
import com.school.config.presentation.dto.ConfigurationRequestDTO;
import com.school.config.presentation.dto.ConfigurationResponseDTO;
import com.school.config.presentation.dto.ConfigurationUpdateRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Application service for configuration management.
 * Implements caching strategy with Redis.
 *
 * @author Backend Developer Agent
 * @version 1.0.0
 * @since 2026-02-04
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ConfigurationService {

    private final ConfigurationRepository configurationRepository;
    private final ConfigurationDTOMapper configurationDTOMapper;

    /**
     * Creates a new configuration.
     *
     * @param requestDTO configuration request
     * @return created configuration
     * @throws IllegalArgumentException if configuration already exists
     */
    @Transactional
    @CacheEvict(value = "configurations", key = "#requestDTO.category()")
    public ConfigurationResponseDTO createConfiguration(ConfigurationRequestDTO requestDTO) {
        log.info("Creating configuration: {}/{}", requestDTO.category(), requestDTO.key());

        // Check for duplicates
        ConfigCategory category = ConfigCategory.valueOf(requestDTO.category());
        if (configurationRepository.existsByCategoryAndKey(category, requestDTO.key())) {
            throw new IllegalArgumentException(
                String.format("Configuration already exists: %s/%s", category, requestDTO.key())
            );
        }

        Configuration configuration = configurationDTOMapper.toDomain(requestDTO);
        Configuration saved = configurationRepository.save(configuration);

        log.info("Configuration created: {}/{} with ID: {}",
            saved.getCategory(), saved.getKey(), saved.getId());

        return configurationDTOMapper.toResponseDTO(saved);
    }

    /**
     * Gets configuration by ID.
     *
     * @param id configuration ID
     * @return configuration
     * @throws RuntimeException if not found
     */
    @Transactional(readOnly = true)
    public ConfigurationResponseDTO getConfigurationById(Long id) {
        log.debug("Fetching configuration by ID: {}", id);

        Configuration configuration = configurationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Configuration not found with ID: " + id));

        return configurationDTOMapper.toResponseDTO(configuration);
    }

    /**
     * Gets grouped configurations by category.
     * Returns a map of key-value pairs.
     * Cached for 5 minutes in Redis.
     *
     * @param category configuration category
     * @return map of configuration key-value pairs
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "configurations", key = "#category")
    public Map<String, String> getGroupedSettings(String category) {
        log.debug("Fetching grouped settings for category: {}", category);

        ConfigCategory configCategory = ConfigCategory.valueOf(category);
        List<Configuration> configurations = configurationRepository.findByCategory(configCategory);

        Map<String, String> grouped = configurations.stream()
            .collect(Collectors.toMap(
                Configuration::getKey,
                Configuration::getValue
            ));

        log.info("Retrieved {} configuration settings for category: {}", grouped.size(), category);

        return grouped;
    }

    /**
     * Gets all configurations with pagination.
     *
     * @param pageable pagination parameters
     * @return page of configurations
     */
    @Transactional(readOnly = true)
    public Page<ConfigurationResponseDTO> getAllConfigurations(Pageable pageable) {
        log.debug("Fetching all configurations, page: {}", pageable.getPageNumber());

        Page<Configuration> configurations = configurationRepository.findAll(pageable);
        return configurations.map(configurationDTOMapper::toResponseDTO);
    }

    /**
     * Updates a configuration.
     * Only value and description can be updated.
     *
     * @param id configuration ID
     * @param updateDTO update request
     * @return updated configuration
     * @throws RuntimeException if not found or version mismatch
     */
    @Transactional
    @CacheEvict(value = "configurations", allEntries = true)
    public ConfigurationResponseDTO updateConfiguration(Long id, ConfigurationUpdateRequestDTO updateDTO) {
        log.info("Updating configuration: {}", id);

        Configuration configuration = configurationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Configuration not found with ID: " + id));

        // Optimistic locking check
        if (!configuration.getVersion().equals(updateDTO.version())) {
            throw new RuntimeException("Optimistic locking failure: version mismatch");
        }

        // Update allowed fields
        configuration.updateValue(updateDTO.value());
        configuration.updateDescription(updateDTO.description());

        Configuration updated = configurationRepository.save(configuration);

        log.info("Configuration updated: {}/{}", updated.getCategory(), updated.getKey());

        return configurationDTOMapper.toResponseDTO(updated);
    }

    /**
     * Deletes a configuration.
     *
     * @param id configuration ID
     */
    @Transactional
    @CacheEvict(value = "configurations", allEntries = true)
    public void deleteConfiguration(Long id) {
        log.info("Deleting configuration: {}", id);

        if (!configurationRepository.findById(id).isPresent()) {
            throw new RuntimeException("Configuration not found with ID: " + id);
        }

        configurationRepository.deleteById(id);

        log.info("Configuration deleted: {}", id);
    }
}
