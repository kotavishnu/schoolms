package com.schoolms.configuration.domain.repository;

import com.schoolms.configuration.domain.model.ConfigCategory;
import com.schoolms.configuration.domain.model.Configuration;
import java.util.List;
import java.util.Optional;

/**
 * Configuration Repository Interface (BE-028)
 * Defines domain repository contract
 */
public interface ConfigurationRepository {

    // Commands
    Configuration save(Configuration config);
    void delete(Long id);

    // Queries
    Optional<Configuration> findById(Long id);
    Optional<Configuration> findByCategoryAndKey(ConfigCategory category, String key);
    List<Configuration> findAll();
    List<Configuration> findByCategory(ConfigCategory category);

    // Existence checks
    boolean existsByCategoryAndKey(ConfigCategory category, String key);
}
