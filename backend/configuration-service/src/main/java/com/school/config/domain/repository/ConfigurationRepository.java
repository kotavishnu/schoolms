package com.school.config.domain.repository;

import com.school.config.domain.model.ConfigCategory;
import com.school.config.domain.model.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Configuration aggregate.
 * Defines persistence contracts in domain layer (Port in Hexagonal Architecture).
 * Actual implementation resides in infrastructure layer.
 *
 * @author Backend Developer Agent
 * @version 1.0.0
 * @since 2026-02-04
 */
public interface ConfigurationRepository {

    /**
     * Saves a configuration.
     *
     * @param configuration configuration to save
     * @return saved configuration with generated ID
     */
    Configuration save(Configuration configuration);

    /**
     * Finds configuration by ID.
     *
     * @param id configuration ID
     * @return optional configuration
     */
    Optional<Configuration> findById(Long id);

    /**
     * Finds configuration by category and key.
     * Used to enforce uniqueness constraint.
     *
     * @param category configuration category
     * @param key configuration key
     * @return optional configuration
     */
    Optional<Configuration> findByCategoryAndKey(ConfigCategory category, String key);

    /**
     * Finds all configurations by category.
     *
     * @param category configuration category
     * @return list of configurations
     */
    List<Configuration> findByCategory(ConfigCategory category);

    /**
     * Finds all configurations with pagination.
     *
     * @param pageable pagination parameters
     * @return page of configurations
     */
    Page<Configuration> findAll(Pageable pageable);

    /**
     * Deletes a configuration by ID.
     *
     * @param id configuration ID
     */
    void deleteById(Long id);

    /**
     * Checks if configuration exists by category and key.
     * Used for uniqueness validation.
     *
     * @param category configuration category
     * @param key configuration key
     * @return true if exists, false otherwise
     */
    boolean existsByCategoryAndKey(ConfigCategory category, String key);

    /**
     * Checks if configuration exists by category and key excluding specific ID.
     * Used for uniqueness validation during updates.
     *
     * @param category configuration category
     * @param key configuration key
     * @param id configuration ID to exclude
     * @return true if exists, false otherwise
     */
    boolean existsByCategoryAndKeyAndIdNot(ConfigCategory category, String key, Long id);
}
