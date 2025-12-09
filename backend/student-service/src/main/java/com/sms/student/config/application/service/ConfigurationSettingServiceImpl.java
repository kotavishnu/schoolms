package com.sms.student.config.application.service;

import com.sms.shared.exception.DuplicateResourceException;
import com.sms.shared.exception.ResourceNotFoundException;
import com.sms.student.config.application.mapper.ConfigurationSettingMapper;
import com.sms.student.config.persistence.entity.ConfigurationSettingEntity;
import com.sms.student.config.persistence.repository.ConfigRepository;
import com.sms.student.config.presentation.dto.ConfigurationSettingDTO;
import com.sms.student.config.presentation.dto.ConfigurationSettingRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Implementation of ConfigurationSettingService.
 * Handles business logic for configuration settings management.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ConfigurationSettingServiceImpl implements ConfigurationSettingService {

    private final ConfigRepository configRepository;
    private final ConfigurationSettingMapper mapper;

    @Override
    public ConfigurationSettingDTO createSetting(ConfigurationSettingRequest request) {
        log.info("Creating configuration setting: {}:{}", request.category(), request.key());

        // Check if setting already exists
        if (configRepository.existsByCategoryAndKey(request.category(), request.key())) {
            throw new DuplicateResourceException(
                "Configuration Setting",
                request.category() + ":" + request.key(),
                "already exists"
            );
        }

        ConfigurationSettingEntity entity = mapper.requestToEntity(request);
        entity.setUpdatedAt(LocalDateTime.now());

        ConfigurationSettingEntity savedEntity = configRepository.save(entity);
        log.info("Configuration setting created successfully: {}:{}", request.category(), request.key());

        return mapper.entityToDTO(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public ConfigurationSettingDTO getSetting(Long id) {
        log.info("Fetching configuration setting with ID: {}", id);

        ConfigurationSettingEntity entity = configRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Configuration Setting", id.toString()));

        return mapper.entityToDTO(entity);
    }

    @Override
    public ConfigurationSettingDTO updateSetting(Long id, ConfigurationSettingRequest request) {
        log.info("Updating configuration setting with ID: {}", id);

        ConfigurationSettingEntity entity = configRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Configuration Setting", id.toString()));

        // Update fields
        entity.setCategory(request.category());
        entity.setKey(request.key());
        entity.setValue(request.value());
        entity.setDescription(request.description());
        entity.setUpdatedAt(LocalDateTime.now());

        ConfigurationSettingEntity updatedEntity = configRepository.save(entity);
        log.info("Configuration setting updated successfully: {}", id);

        return mapper.entityToDTO(updatedEntity);
    }

    @Override
    public void deleteSetting(Long id) {
        log.info("Deleting configuration setting with ID: {}", id);

        ConfigurationSettingEntity entity = configRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Configuration Setting", id.toString()));

        configRepository.delete(entity);
        log.info("Configuration setting deleted successfully: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ConfigurationSettingDTO> getSettingsByCategory(String category, int page, int size) {
        log.info("Fetching configuration settings for category: '{}', page: {}, size: {}", category, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<ConfigurationSettingEntity> result;

        // If category is empty or blank, return all settings
        if (category == null || category.trim().isEmpty()) {
            result = configRepository.findAll(pageable);
        } else {
            result = configRepository.findByCategory(category, pageable);
        }

        return result.map(mapper::entityToDTO);
    }
}
