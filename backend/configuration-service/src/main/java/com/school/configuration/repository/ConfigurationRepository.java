package com.school.configuration.repository;

import com.school.configuration.domain.entity.ConfigCategory;
import com.school.configuration.domain.entity.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Configuration Repository
 *
 * Data access layer for Configuration entity.
 * Provides query methods for configuration CRUD operations,
 * uniqueness checks, and category-based retrieval.
 */
@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, Long> {

    // ==================== Composite Key Lookup ====================

    /**
     * Find configuration by category and key (composite business key)
     * This is the preferred lookup method for client requests
     *
     * @param category Configuration category
     * @param key Configuration key
     * @return Optional configuration
     */
    Optional<Configuration> findByCategoryAndKey(ConfigCategory category, String key);

    // ==================== Category Queries ====================

    /**
     * Find all configurations by category
     * Returns all settings in a specific category
     *
     * @param category Configuration category
     * @return List of configurations
     */
    List<Configuration> findByCategory(ConfigCategory category);

    /**
     * Find all configurations ordered by category and key
     * Used for listing all settings in a structured way
     *
     * @return List of all configurations sorted by category, then key
     */
    List<Configuration> findAllByOrderByCategoryAscKeyAsc();

    // ==================== Uniqueness Checks ====================

    /**
     * Check if configuration exists by category and key
     * Used for duplicate detection during upsert operations
     *
     * @param category Configuration category
     * @param key Configuration key
     * @return true if exists, false otherwise
     */
    boolean existsByCategoryAndKey(ConfigCategory category, String key);

    // ==================== Delete Operations ====================

    /**
     * Delete configuration by category and key
     * Returns number of records deleted (0 or 1)
     *
     * @param category Configuration category
     * @param key Configuration key
     * @return Number of configurations deleted
     */
    Long deleteByCategoryAndKey(ConfigCategory category, String key);
}
