package com.school.sms.configuration.domain.repository;

import com.school.sms.configuration.domain.entity.ConfigurationSetting;
import com.school.sms.configuration.domain.enums.SettingCategory;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository interface for ConfigurationSetting.
 * This is implemented by the infrastructure layer.
 */
public interface ConfigurationSettingRepository {

    /**
     * Save a configuration setting.
     *
     * @param setting the setting to save
     * @return the saved setting
     */
    ConfigurationSetting save(ConfigurationSetting setting);

    /**
     * Find a setting by its ID.
     *
     * @param id the setting ID
     * @return optional containing the setting if found
     */
    Optional<ConfigurationSetting> findById(Long id);

    /**
     * Find a setting by category and key.
     *
     * @param category the setting category
     * @param key      the setting key
     * @return optional containing the setting if found
     */
    Optional<ConfigurationSetting> findByCategoryAndKey(SettingCategory category, String key);

    /**
     * Find all settings by category.
     *
     * @param category the setting category
     * @return list of settings
     */
    List<ConfigurationSetting> findByCategory(SettingCategory category);

    /**
     * Find all settings.
     *
     * @return list of all settings
     */
    List<ConfigurationSetting> findAll();

    /**
     * Check if a setting exists by category and key.
     *
     * @param category the setting category
     * @param key      the setting key
     * @return true if exists, false otherwise
     */
    boolean existsByCategoryAndKey(SettingCategory category, String key);

    /**
     * Delete a setting by ID.
     *
     * @param id the setting ID
     */
    void deleteById(Long id);

    /**
     * Count total number of settings.
     *
     * @return count of settings
     */
    long count();
}
