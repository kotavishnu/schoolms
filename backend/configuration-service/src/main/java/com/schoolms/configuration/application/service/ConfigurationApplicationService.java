package com.schoolms.configuration.application.service;

import com.schoolms.configuration.application.mapper.ConfigurationMapper;
import com.schoolms.configuration.domain.exception.ConfigurationNotFoundException;
import com.schoolms.configuration.domain.exception.DuplicateConfigKeyException;
import com.schoolms.configuration.domain.model.ConfigCategory;
import com.schoolms.configuration.domain.model.Configuration;
import com.schoolms.configuration.domain.repository.ConfigurationRepository;
import com.schoolms.configuration.presentation.dto.ConfigurationListResponse;
import com.schoolms.configuration.presentation.dto.ConfigurationResponse;
import com.schoolms.configuration.presentation.dto.CreateConfigurationRequest;
import com.schoolms.configuration.presentation.dto.UpdateConfigurationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Configuration Application Service (BE-036)
 * Configuration use cases
 */
@Service
@Transactional
public class ConfigurationApplicationService {

    private static final Logger log = LoggerFactory.getLogger(ConfigurationApplicationService.class);

    private final ConfigurationRepository configurationRepository;
    private final ConfigurationMapper configurationMapper;

    public ConfigurationApplicationService(
        ConfigurationRepository configurationRepository,
        ConfigurationMapper configurationMapper
    ) {
        this.configurationRepository = configurationRepository;
        this.configurationMapper = configurationMapper;
    }

    /**
     * Command: Create Configuration
     */
    public ConfigurationResponse createConfiguration(CreateConfigurationRequest request) {
        log.info("Creating configuration: category={}, key={}", request.category(), request.key());

        // Check uniqueness
        if (configurationRepository.existsByCategoryAndKey(request.category(), request.key())) {
            throw new DuplicateConfigKeyException(request.category(), request.key());
        }

        // Create domain entity
        Configuration configuration = Configuration.create(
            request.category(),
            request.key(),
            request.value(),
            request.description()
        );

        // Persist
        configuration = configurationRepository.save(configuration);

        log.info("Configuration created successfully: id={}", configuration.getId());

        return configurationMapper.toResponse(configuration);
    }

    /**
     * Command: Update Configuration
     */
    public ConfigurationResponse updateConfiguration(Long id, UpdateConfigurationRequest request) {
        log.info("Updating configuration: id={}", id);

        Configuration configuration = configurationRepository.findById(id)
            .orElseThrow(() -> new ConfigurationNotFoundException(id));

        // Update domain entity
        if (request.value() != null) {
            configuration.updateValue(request.value());
        }
        if (request.description() != null) {
            configuration.updateDescription(request.description());
        }

        // Persist
        configuration = configurationRepository.save(configuration);

        log.info("Configuration updated successfully: id={}", id);

        return configurationMapper.toResponse(configuration);
    }

    /**
     * Command: Delete Configuration
     */
    public void deleteConfiguration(Long id) {
        log.info("Deleting configuration: id={}", id);

        if (!configurationRepository.findById(id).isPresent()) {
            throw new ConfigurationNotFoundException(id);
        }

        configurationRepository.delete(id);

        log.info("Configuration deleted successfully: id={}", id);
    }

    /**
     * Query: Get Configuration by ID
     */
    @Transactional(readOnly = true)
    public ConfigurationResponse getConfiguration(Long id) {
        log.debug("Fetching configuration: id={}", id);

        Configuration configuration = configurationRepository.findById(id)
            .orElseThrow(() -> new ConfigurationNotFoundException(id));

        return configurationMapper.toResponse(configuration);
    }

    /**
     * Query: List Configurations
     */
    @Transactional(readOnly = true)
    public ConfigurationListResponse listConfigurations(ConfigCategory category) {
        log.debug("Listing configurations: category={}", category);

        List<Configuration> configurations = category != null
            ? configurationRepository.findByCategory(category)
            : configurationRepository.findAll();

        List<ConfigurationResponse> responses = configurationMapper.toResponseList(configurations);

        return new ConfigurationListResponse(
            responses,
            responses.size()
        );
    }
}
