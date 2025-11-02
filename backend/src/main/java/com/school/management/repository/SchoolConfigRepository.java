package com.school.management.repository;

import com.school.management.model.SchoolConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for SchoolConfig entity.
 *
 * Provides CRUD operations and custom queries for school configuration management.
 *
 * @author School Management Team
 */
@Repository
public interface SchoolConfigRepository extends JpaRepository<SchoolConfig, Long> {

    /**
     * Find configuration by key
     * @param configKey Configuration key
     * @return Optional configuration
     */
    Optional<SchoolConfig> findByConfigKey(String configKey);

    /**
     * Find configurations by category
     * @param category Configuration category
     * @return List of configurations
     */
    List<SchoolConfig> findByCategory(String category);

    /**
     * Find editable configurations
     * @param isEditable Editable flag
     * @return List of editable configurations
     */
    List<SchoolConfig> findByIsEditable(Boolean isEditable);

    /**
     * Find configurations by category and editable status
     * @param category Configuration category
     * @param isEditable Editable flag
     * @return List of configurations
     */
    List<SchoolConfig> findByCategoryAndIsEditable(String category, Boolean isEditable);

    /**
     * Check if configuration key exists
     * @param configKey Configuration key
     * @return true if exists
     */
    boolean existsByConfigKey(String configKey);
}
