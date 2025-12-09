package com.sms.student.config.application.service;

import com.sms.student.config.presentation.dto.ConfigurationSettingDTO;
import com.sms.student.config.presentation.dto.ConfigurationSettingRequest;
import org.springframework.data.domain.Page;

/**
 * Service interface for configuration settings management.
 */
public interface ConfigurationSettingService {

    /**
     * Create a new configuration setting.
     *
     * @param request the request containing setting details
     * @return the created configuration setting DTO
     * @throws com.sms.shared.exception.DuplicateResourceException if setting already exists
     */
    ConfigurationSettingDTO createSetting(ConfigurationSettingRequest request);

    /**
     * Get configuration setting by ID.
     *
     * @param id the setting ID
     * @return the configuration setting DTO
     * @throws com.sms.shared.exception.ResourceNotFoundException if setting not found
     */
    ConfigurationSettingDTO getSetting(Long id);

    /**
     * Update configuration setting by ID.
     *
     * @param id the setting ID
     * @param request the update request
     * @return the updated configuration setting DTO
     * @throws com.sms.shared.exception.ResourceNotFoundException if setting not found
     */
    ConfigurationSettingDTO updateSetting(Long id, ConfigurationSettingRequest request);

    /**
     * Delete configuration setting by ID.
     *
     * @param id the setting ID
     * @throws com.sms.shared.exception.ResourceNotFoundException if setting not found
     */
    void deleteSetting(Long id);

    /**
     * Get all settings for a category.
     *
     * @param category the setting category
     * @param page the page number (0-indexed)
     * @param size the page size
     * @return page of settings for the category
     */
    Page<ConfigurationSettingDTO> getSettingsByCategory(String category, int page, int size);
}
