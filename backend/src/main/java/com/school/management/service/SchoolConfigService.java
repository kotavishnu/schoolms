package com.school.management.service;

import com.school.management.dto.request.SchoolConfigRequestDTO;
import com.school.management.dto.response.SchoolConfigResponseDTO;
import com.school.management.exception.DuplicateResourceException;
import com.school.management.exception.ResourceNotFoundException;
import com.school.management.exception.ValidationException;
import com.school.management.mapper.SchoolConfigMapper;
import com.school.management.model.SchoolConfig;
import com.school.management.repository.SchoolConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service layer for School Configuration management operations.
 *
 * Handles business logic for configuration CRUD, validation, and category-based filtering.
 *
 * @author School Management Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SchoolConfigService {

    private final SchoolConfigRepository schoolConfigRepository;
    private final SchoolConfigMapper schoolConfigMapper;

    /**
     * Create a new school configuration
     *
     * @param requestDTO Configuration data
     * @return Created configuration DTO
     * @throws DuplicateResourceException if config key already exists
     */
    @Transactional
    public SchoolConfigResponseDTO createConfig(SchoolConfigRequestDTO requestDTO) {
        log.info("Creating school config: {}", requestDTO.getConfigKey());

        // Validate config key uniqueness
        if (schoolConfigRepository.existsByConfigKey(requestDTO.getConfigKey())) {
            throw new DuplicateResourceException("SchoolConfig", "configKey", requestDTO.getConfigKey());
        }

        // Map DTO to entity
        SchoolConfig config = schoolConfigMapper.toEntity(requestDTO);

        // Set default category if not provided
        if (config.getCategory() == null || config.getCategory().isBlank()) {
            config.setCategory("GENERAL");
        }

        // Save configuration
        SchoolConfig savedConfig = schoolConfigRepository.save(config);

        log.info("School config created successfully with ID: {}", savedConfig.getId());

        return schoolConfigMapper.toResponseDTO(savedConfig);
    }

    /**
     * Get configuration by ID
     *
     * @param id Configuration ID
     * @return Configuration DTO
     * @throws ResourceNotFoundException if configuration not found
     */
    public SchoolConfigResponseDTO getConfig(Long id) {
        log.debug("Fetching school config with ID: {}", id);

        SchoolConfig config = schoolConfigRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SchoolConfig", "id", id));

        return schoolConfigMapper.toResponseDTO(config);
    }

    /**
     * Get configuration by key
     *
     * @param key Configuration key
     * @return Configuration DTO
     * @throws ResourceNotFoundException if configuration not found
     */
    public SchoolConfigResponseDTO getConfigByKey(String key) {
        log.debug("Fetching school config with key: {}", key);

        SchoolConfig config = schoolConfigRepository.findByConfigKey(key)
                .orElseThrow(() -> new ResourceNotFoundException("SchoolConfig", "configKey", key));

        return schoolConfigMapper.toResponseDTO(config);
    }

    /**
     * Get configuration value by key
     *
     * @param key Configuration key
     * @return Configuration value as string
     * @throws ResourceNotFoundException if configuration not found
     */
    public String getConfigValue(String key) {
        log.debug("Fetching config value for key: {}", key);

        SchoolConfig config = schoolConfigRepository.findByConfigKey(key)
                .orElseThrow(() -> new ResourceNotFoundException("SchoolConfig", "configKey", key));

        return config.getConfigValue();
    }

    /**
     * Get all configurations
     *
     * @return List of configuration DTOs
     */
    public List<SchoolConfigResponseDTO> getAllConfigs() {
        log.debug("Fetching all school configurations");

        List<SchoolConfig> configs = schoolConfigRepository.findAll();

        return schoolConfigMapper.toResponseDTOList(configs);
    }

    /**
     * Get configurations by category
     *
     * @param category Configuration category
     * @return List of configuration DTOs in the specified category
     */
    public List<SchoolConfigResponseDTO> getConfigsByCategory(String category) {
        log.debug("Fetching school configs by category: {}", category);

        List<SchoolConfig> configs = schoolConfigRepository.findByCategory(category);

        return schoolConfigMapper.toResponseDTOList(configs);
    }

    /**
     * Get editable configurations
     *
     * @return List of editable configuration DTOs
     */
    public List<SchoolConfigResponseDTO> getEditableConfigs() {
        log.debug("Fetching editable school configurations");

        List<SchoolConfig> configs = schoolConfigRepository.findByIsEditable(true);

        return schoolConfigMapper.toResponseDTOList(configs);
    }

    /**
     * Update existing configuration
     *
     * @param id Configuration ID
     * @param requestDTO Updated configuration data
     * @return Updated configuration DTO
     * @throws ResourceNotFoundException if configuration not found
     * @throws ValidationException if attempting to update non-editable config
     * @throws DuplicateResourceException if config key already in use by another config
     */
    @Transactional
    public SchoolConfigResponseDTO updateConfig(Long id, SchoolConfigRequestDTO requestDTO) {
        log.info("Updating school config with ID: {}", id);

        // Fetch existing configuration
        SchoolConfig config = schoolConfigRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SchoolConfig", "id", id));

        // Check if configuration is editable
        if (Boolean.FALSE.equals(config.getIsEditable())) {
            throw new ValidationException("Cannot update system configuration: " + config.getConfigKey());
        }

        // Validate config key uniqueness (excluding current config)
        schoolConfigRepository.findByConfigKey(requestDTO.getConfigKey())
                .filter(c -> !c.getId().equals(id))
                .ifPresent(c -> {
                    throw new DuplicateResourceException("SchoolConfig", "configKey", requestDTO.getConfigKey());
                });

        // Update fields
        schoolConfigMapper.updateEntityFromDTO(requestDTO, config);

        SchoolConfig updatedConfig = schoolConfigRepository.save(config);

        log.info("School config updated successfully with ID: {}", updatedConfig.getId());

        return schoolConfigMapper.toResponseDTO(updatedConfig);
    }

    /**
     * Update configuration value only
     *
     * @param key Configuration key
     * @param value New value
     * @return Updated configuration DTO
     * @throws ResourceNotFoundException if configuration not found
     * @throws ValidationException if attempting to update non-editable config
     */
    @Transactional
    public SchoolConfigResponseDTO updateConfigValue(String key, String value) {
        log.info("Updating config value for key: {}", key);

        SchoolConfig config = schoolConfigRepository.findByConfigKey(key)
                .orElseThrow(() -> new ResourceNotFoundException("SchoolConfig", "configKey", key));

        // Check if configuration is editable
        if (Boolean.FALSE.equals(config.getIsEditable())) {
            throw new ValidationException("Cannot update system configuration: " + config.getConfigKey());
        }

        config.setConfigValue(value);

        SchoolConfig updatedConfig = schoolConfigRepository.save(config);

        log.info("Config value updated successfully for key: {}", key);

        return schoolConfigMapper.toResponseDTO(updatedConfig);
    }

    /**
     * Delete configuration
     *
     * @param id Configuration ID
     * @throws ResourceNotFoundException if configuration not found
     * @throws ValidationException if attempting to delete non-editable config
     */
    @Transactional
    public void deleteConfig(Long id) {
        log.info("Deleting school config with ID: {}", id);

        SchoolConfig config = schoolConfigRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SchoolConfig", "id", id));

        // Check if configuration is editable
        if (Boolean.FALSE.equals(config.getIsEditable())) {
            throw new ValidationException("Cannot delete system configuration: " + config.getConfigKey());
        }

        schoolConfigRepository.deleteById(id);

        log.info("School config deleted successfully with ID: {}", id);
    }

    /**
     * Check if configuration key exists
     *
     * @param key Configuration key
     * @return true if key exists
     */
    public boolean configExists(String key) {
        return schoolConfigRepository.existsByConfigKey(key);
    }
}
