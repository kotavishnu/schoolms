package com.school.sms.configuration.infrastructure.persistence.repository;

import com.school.sms.configuration.infrastructure.persistence.entity.ConfigurationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for ConfigurationJpaEntity.
 *
 * <p>This interface provides CRUD operations and custom query methods
 * for configuration persistence using Spring Data JPA.</p>
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
@Repository
public interface ConfigurationJpaRepository extends JpaRepository<ConfigurationJpaEntity, Long> {

    /**
     * Finds a configuration by category and key.
     *
     * @param category the configuration category
     * @param configKey the configuration key
     * @return an Optional containing the configuration if found
     */
    Optional<ConfigurationJpaEntity> findByCategoryAndConfigKey(
        ConfigurationJpaEntity.CategoryEnum category,
        String configKey
    );

    /**
     * Finds all configurations in a specific category.
     *
     * @param category the configuration category
     * @return a list of configurations in the category
     */
    List<ConfigurationJpaEntity> findByCategory(ConfigurationJpaEntity.CategoryEnum category);

    /**
     * Deletes a configuration by category and key.
     *
     * @param category the configuration category
     * @param configKey the configuration key
     */
    @Modifying
    @Query("DELETE FROM ConfigurationJpaEntity c WHERE c.category = :category AND c.configKey = :configKey")
    void deleteByCategoryAndConfigKey(
        @Param("category") ConfigurationJpaEntity.CategoryEnum category,
        @Param("configKey") String configKey
    );

    /**
     * Checks if a configuration exists.
     *
     * @param category the configuration category
     * @param configKey the configuration key
     * @return true if exists, false otherwise
     */
    boolean existsByCategoryAndConfigKey(
        ConfigurationJpaEntity.CategoryEnum category,
        String configKey
    );
}
