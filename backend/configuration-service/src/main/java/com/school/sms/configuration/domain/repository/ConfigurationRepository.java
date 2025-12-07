package com.school.sms.configuration.domain.repository;

import com.school.sms.configuration.domain.model.Category;
import com.school.sms.configuration.domain.model.ConfigurationSetting;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for ConfigurationSetting domain entities.
 *
 * <p>This is a domain-layer interface that defines the contract for
 * configuration persistence operations. The infrastructure layer will
 * provide the concrete implementation using JPA.</p>
 *
 * <p>This repository supports UPSERT operations - the save method will
 * insert a new configuration if it doesn't exist, or update if it does.</p>
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
public interface ConfigurationRepository {

    /**
     * Saves a configuration setting (INSERT or UPDATE).
     *
     * <p>This performs an UPSERT operation:</p>
     * <ul>
     *   <li>If a configuration with the same category and key exists, it will be updated</li>
     *   <li>Otherwise, a new configuration will be created</li>
     * </ul>
     *
     * @param configuration the configuration setting to save
     * @return the saved configuration setting with populated ID and version
     */
    ConfigurationSetting save(ConfigurationSetting configuration);

    /**
     * Finds a configuration by category and key.
     *
     * @param category the configuration category
     * @param key the configuration key
     * @return an Optional containing the configuration if found, empty otherwise
     */
    Optional<ConfigurationSetting> findByCategoryAndKey(Category category, String key);

    /**
     * Finds all configurations in a specific category.
     *
     * @param category the configuration category
     * @return a list of configuration settings in the category (empty list if none found)
     */
    List<ConfigurationSetting> findByCategory(Category category);

    /**
     * Finds all configuration settings.
     *
     * @return a list of all configuration settings (empty list if none exist)
     */
    List<ConfigurationSetting> findAll();

    /**
     * Deletes a configuration by category and key.
     *
     * @param category the configuration category
     * @param key the configuration key
     */
    void deleteByCategoryAndKey(Category category, String key);

    /**
     * Checks if a configuration exists.
     *
     * @param category the configuration category
     * @param key the configuration key
     * @return true if the configuration exists, false otherwise
     */
    boolean existsByCategoryAndKey(Category category, String key);
}
