package com.school.config.infrastructure.persistence.adapter;

import com.school.config.domain.model.ConfigCategory;
import com.school.config.domain.model.Configuration;
import com.school.config.domain.repository.ConfigurationRepository;
import com.school.config.infrastructure.persistence.entity.ConfigurationEntity;
import com.school.config.infrastructure.persistence.mapper.ConfigurationEntityMapper;
import com.school.config.infrastructure.persistence.repository.JpaConfigurationRepositoryInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter implementing ConfigurationRepository using Spring Data JPA.
 * Bridges domain layer with JPA persistence infrastructure.
 *
 * @author Backend Developer Agent
 * @version 1.0.0
 * @since 2026-02-04
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JpaConfigurationRepositoryAdapter implements ConfigurationRepository {

    private final JpaConfigurationRepositoryInterface jpaRepository;
    private final ConfigurationEntityMapper mapper;

    @Override
    public Configuration save(Configuration configuration) {
        log.debug("Saving configuration: {}/{}",
            configuration.getCategory(), configuration.getKey());

        ConfigurationEntity entity = mapper.toEntity(configuration);
        ConfigurationEntity savedEntity = jpaRepository.save(entity);

        log.debug("Configuration saved with ID: {}", savedEntity.getId());

        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Configuration> findById(Long id) {
        log.debug("Finding configuration by ID: {}", id);

        return jpaRepository.findById(id)
            .map(mapper::toDomain);
    }

    @Override
    public Optional<Configuration> findByCategoryAndKey(ConfigCategory category, String key) {
        log.debug("Finding configuration by category: {} and key: {}", category, key);

        return jpaRepository.findByCategoryAndKey(category, key)
            .map(mapper::toDomain);
    }

    @Override
    public List<Configuration> findByCategory(ConfigCategory category) {
        log.debug("Finding all configurations by category: {}", category);

        List<ConfigurationEntity> entities = jpaRepository.findByCategory(category);

        return entities.stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public Page<Configuration> findAll(Pageable pageable) {
        log.debug("Finding all configurations with pagination: page {}", pageable.getPageNumber());

        Page<ConfigurationEntity> entities = jpaRepository.findAll(pageable);

        return entities.map(mapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        log.debug("Deleting configuration by ID: {}", id);

        jpaRepository.deleteById(id);

        log.info("Configuration deleted: {}", id);
    }

    @Override
    public boolean existsByCategoryAndKey(ConfigCategory category, String key) {
        return jpaRepository.existsByCategoryAndKey(category, key);
    }

    @Override
    public boolean existsByCategoryAndKeyAndIdNot(ConfigCategory category, String key, Long id) {
        return jpaRepository.existsByCategoryAndKeyAndIdNot(category, key, id);
    }
}
