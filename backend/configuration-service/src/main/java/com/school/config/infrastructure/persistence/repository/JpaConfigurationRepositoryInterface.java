package com.school.config.infrastructure.persistence.repository;

import com.school.config.domain.model.ConfigCategory;
import com.school.config.infrastructure.persistence.entity.ConfigurationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository interface for ConfigurationEntity.
 * Provides CRUD operations and custom queries.
 *
 * @author Backend Developer Agent
 * @version 1.0.0
 * @since 2026-02-04
 */
@Repository
public interface JpaConfigurationRepositoryInterface extends JpaRepository<ConfigurationEntity, Long> {

    /**
     * Finds configuration by category and key.
     *
     * @param category configuration category
     * @param key configuration key
     * @return optional configuration entity
     */
    Optional<ConfigurationEntity> findByCategoryAndKey(ConfigCategory category, String key);

    /**
     * Finds all configurations by category.
     *
     * @param category configuration category
     * @return list of configuration entities
     */
    List<ConfigurationEntity> findByCategory(ConfigCategory category);

    /**
     * Checks if configuration exists by category and key.
     *
     * @param category configuration category
     * @param key configuration key
     * @return true if exists
     */
    boolean existsByCategoryAndKey(ConfigCategory category, String key);

    /**
     * Checks if configuration exists by category and key excluding specific ID.
     *
     * @param category configuration category
     * @param key configuration key
     * @param id configuration ID to exclude
     * @return true if exists
     */
    boolean existsByCategoryAndKeyAndIdNot(ConfigCategory category, String key, Long id);
}
