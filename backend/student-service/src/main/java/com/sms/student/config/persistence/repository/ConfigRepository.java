package com.sms.student.config.persistence.repository;

import com.sms.student.config.persistence.entity.ConfigurationSettingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for ConfigurationSettingEntity.
 * Provides database operations for configuration management.
 */
@Repository
public interface ConfigRepository extends JpaRepository<ConfigurationSettingEntity, Long> {

    /**
     * Find configuration by category and key.
     *
     * @param category the configuration category
     * @param key the configuration key
     * @return Optional containing the configuration if found
     */
    Optional<ConfigurationSettingEntity> findByCategoryAndKey(String category, String key);

    /**
     * Find all configurations for a given category.
     *
     * @param category the configuration category
     * @param pageable pagination information
     * @return page of configurations for the category
     */
    Page<ConfigurationSettingEntity> findByCategory(String category, Pageable pageable);

    /**
     * Check if a configuration with the given category and key exists.
     *
     * @param category the configuration category
     * @param key the configuration key
     * @return true if exists, false otherwise
     */
    boolean existsByCategoryAndKey(String category, String key);
}
