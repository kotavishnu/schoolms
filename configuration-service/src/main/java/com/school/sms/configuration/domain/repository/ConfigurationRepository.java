package com.school.sms.configuration.domain.repository;

import com.school.sms.configuration.domain.model.Configuration;
import com.school.sms.configuration.domain.model.ConfigurationCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository interface (Port) for Configuration
 * Defines contract for configuration persistence
 */
public interface ConfigurationRepository {

    Configuration save(Configuration configuration);

    Optional<Configuration> findById(Long id);

    List<Configuration> findByCategory(ConfigurationCategory category);

    Page<Configuration> findAll(Pageable pageable);

    boolean existsByCategoryAndConfigKey(ConfigurationCategory category, String configKey);

    void deleteById(Long id);
}
