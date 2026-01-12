package com.schoolms.configuration.infrastructure.persistence.repository;

import com.schoolms.configuration.domain.model.ConfigCategory;
import com.schoolms.configuration.infrastructure.persistence.entity.ConfigurationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

/**
 * Configuration JPA Repository (BE-031)
 * Spring Data JPA repository
 */
public interface ConfigurationJpaRepository extends JpaRepository<ConfigurationJpaEntity, Long> {

    Optional<ConfigurationJpaEntity> findByCategoryAndKey(ConfigCategory category, String key);

    List<ConfigurationJpaEntity> findByCategory(ConfigCategory category);

    boolean existsByCategoryAndKey(ConfigCategory category, String key);
}
