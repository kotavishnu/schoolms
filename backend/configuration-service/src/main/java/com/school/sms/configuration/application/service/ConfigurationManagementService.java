package com.school.sms.configuration.application.service;

import com.school.sms.configuration.application.dto.*;
import com.school.sms.configuration.application.mapper.ConfigurationMapper;
import com.school.sms.configuration.domain.entity.ConfigurationSetting;
import com.school.sms.configuration.domain.enums.SettingCategory;
import com.school.sms.configuration.domain.exception.DuplicateSettingException;
import com.school.sms.configuration.domain.exception.SettingNotFoundException;
import com.school.sms.configuration.domain.repository.ConfigurationSettingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for managing configuration settings.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ConfigurationManagementService {

    private final ConfigurationSettingRepository repository;
    private final ConfigurationMapper mapper;

    /**
     * Create a new configuration setting.
     *
     * @param request the creation request
     * @param userId  the user creating the setting
     * @return the created setting
     * @throws DuplicateSettingException if setting already exists
     */
    public SettingResponse createSetting(CreateSettingRequest request, String userId) {
        log.info("Creating configuration setting: {}.{}", request.getCategory(), request.getKey());

        // Check for duplicate
        if (repository.existsByCategoryAndKey(request.getCategory(), request.getKey())) {
            throw new DuplicateSettingException(request.getCategory(), request.getKey());
        }

        // Create and save setting
        ConfigurationSetting setting = mapper.toEntity(request);
        setting.setCreatedBy(userId);
        setting.setUpdatedBy(userId);

        ConfigurationSetting savedSetting = repository.save(setting);
        log.info("Configuration setting created successfully: ID={}", savedSetting.getId());

        return mapper.toSettingResponse(savedSetting);
    }

    /**
     * Get configuration setting by ID.
     *
     * @param settingId the setting ID
     * @return the setting
     * @throws SettingNotFoundException if setting not found
     */
    @Transactional(readOnly = true)
    public SettingResponse getSettingById(Long settingId) {
        log.debug("Fetching configuration setting by ID: {}", settingId);

        ConfigurationSetting setting = repository.findById(settingId)
                .orElseThrow(() -> new SettingNotFoundException(settingId));

        return mapper.toSettingResponse(setting);
    }

    /**
     * Update a configuration setting.
     *
     * @param settingId the setting ID
     * @param request   the update request
     * @param userId    the user updating the setting
     * @return the updated setting
     * @throws SettingNotFoundException if setting not found
     */
    public SettingResponse updateSetting(Long settingId, UpdateSettingRequest request, String userId) {
        log.info("Updating configuration setting: ID={}", settingId);

        ConfigurationSetting setting = repository.findById(settingId)
                .orElseThrow(() -> new SettingNotFoundException(settingId));

        // Update fields
        setting.updateValue(request.getValue());
        setting.updateDescription(request.getDescription());
        setting.setUpdatedBy(userId);

        ConfigurationSetting updatedSetting = repository.save(setting);
        log.info("Configuration setting updated successfully: ID={}", settingId);

        return mapper.toSettingResponse(updatedSetting);
    }

    /**
     * Get all settings for a specific category.
     *
     * @param category the category
     * @return settings response
     */
    @Transactional(readOnly = true)
    public CategorySettingsResponse getSettingsByCategory(SettingCategory category) {
        log.debug("Fetching settings for category: {}", category);

        List<ConfigurationSetting> settings = repository.findByCategory(category);
        List<SettingResponse> settingResponses = settings.stream()
                .map(mapper::toSettingResponse)
                .collect(Collectors.toList());

        return CategorySettingsResponse.builder()
                .category(category)
                .settings(settingResponses)
                .build();
    }

    /**
     * Get all settings grouped by category.
     *
     * @return map of category to settings
     */
    @Transactional(readOnly = true)
    public Map<SettingCategory, List<SettingResponse>> getAllSettingsGroupedByCategory() {
        log.debug("Fetching all settings grouped by category");

        List<ConfigurationSetting> allSettings = repository.findAll();

        return allSettings.stream()
                .collect(Collectors.groupingBy(
                        ConfigurationSetting::getCategory,
                        Collectors.mapping(mapper::toSettingResponse, Collectors.toList())
                ));
    }

    /**
     * Delete a configuration setting.
     *
     * @param settingId the setting ID
     * @throws SettingNotFoundException if setting not found
     */
    public void deleteSetting(Long settingId) {
        log.info("Deleting configuration setting: ID={}", settingId);

        // Verify exists
        if (!repository.findById(settingId).isPresent()) {
            throw new SettingNotFoundException(settingId);
        }

        repository.deleteById(settingId);
        log.info("Configuration setting deleted successfully: ID={}", settingId);
    }
}
