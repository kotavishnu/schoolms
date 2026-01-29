package com.school.configuration.domain.repository;

import com.school.configuration.domain.model.ConfigCategory;
import com.school.configuration.domain.model.Configuration;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Configuration domain model.
 * Defines the contract for configuration persistence operations.
 * Implementation will be provided in the infrastructure layer.
 */
public interface ConfigurationRepository {

    /**
     * Saves a configuration.
     *
     * @param configuration the configuration to save
     * @return the saved configuration
     */
    Configuration save(Configuration configuration);

    /**
     * Finds a configuration by its id.
     *
     * @param id the configuration id
     * @return an Optional containing the configuration if found, empty otherwise
     */
    Optional<Configuration> findById(Long id);

    /**
     * Finds a configuration by category and key.
     *
     * @param category the configuration category
     * @param key the configuration key
     * @return an Optional containing the configuration if found, empty otherwise
     */
    Optional<Configuration> findByCategoryAndKey(ConfigCategory category, String key);

    /**
     * Finds all configurations in a specific category.
     *
     * @param category the configuration category
     * @return list of configurations in the category
     */
    List<Configuration> findByCategory(ConfigCategory category);

    /**
     * Finds all configurations.
     *
     * @return list of all configurations
     */
    List<Configuration> findAll();

    /**
     * Deletes a configuration.
     *
     * @param configuration the configuration to delete
     */
    void delete(Configuration configuration);

    /**
     * Checks if a configuration exists by category and key.
     *
     * @param category the configuration category
     * @param key the configuration key
     * @return true if exists, false otherwise
     */
    boolean existsByCategoryAndKey(ConfigCategory category, String key);
}
