package com.school.configuration.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for ConfigurationEntity.
 * Provides database access methods for configuration persistence.
 */
@Repository
public interface ConfigurationJpaRepository extends JpaRepository<ConfigurationEntity, Long> {

    /**
     * Finds a configuration by category and key.
     *
     * @param category the configuration category (as string)
     * @param key the configuration key
     * @return Optional containing the configuration entity if found, empty otherwise
     */
    Optional<ConfigurationEntity> findByCategoryAndKey(String category, String key);

    /**
     * Finds all configurations in a specific category.
     *
     * @param category the configuration category (as string)
     * @return list of configuration entities in the category
     */
    List<ConfigurationEntity> findByCategory(String category);

    /**
     * Checks if a configuration exists by category and key.
     *
     * @param category the configuration category (as string)
     * @param key the configuration key
     * @return true if exists, false otherwise
     */
    boolean existsByCategoryAndKey(String category, String key);
}
