package com.schoolms.configuration.infrastructure.persistence.adapter;

import com.schoolms.configuration.domain.exception.ConfigurationNotFoundException;
import com.schoolms.configuration.domain.model.ConfigCategory;
import com.schoolms.configuration.domain.model.Configuration;
import com.schoolms.configuration.domain.repository.ConfigurationRepository;
import com.schoolms.configuration.infrastructure.persistence.entity.ConfigurationJpaEntity;
import com.schoolms.configuration.infrastructure.persistence.repository.ConfigurationJpaRepository;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Configuration Repository Adapter (BE-033)
 * Implements domain repository
 */
@Component
public class ConfigurationRepositoryAdapter implements ConfigurationRepository {

    private final ConfigurationJpaRepository jpaRepository;
    private final ConfigurationEntityMapper entityMapper;

    public ConfigurationRepositoryAdapter(
        ConfigurationJpaRepository jpaRepository,
        ConfigurationEntityMapper entityMapper
    ) {
        this.jpaRepository = jpaRepository;
        this.entityMapper = entityMapper;
    }

    @Override
    public Configuration save(Configuration config) {
        ConfigurationJpaEntity entity = entityMapper.toJpaEntity(config);
        ConfigurationJpaEntity saved = jpaRepository.save(entity);
        return entityMapper.toDomain(saved);
    }

    @Override
    public void delete(Long id) {
        ConfigurationJpaEntity entity = jpaRepository.findById(id)
            .orElseThrow(() -> new ConfigurationNotFoundException(id));
        jpaRepository.delete(entity);
    }

    @Override
    public Optional<Configuration> findById(Long id) {
        return jpaRepository.findById(id)
            .map(entityMapper::toDomain);
    }

    @Override
    public Optional<Configuration> findByCategoryAndKey(ConfigCategory category, String key) {
        return jpaRepository.findByCategoryAndKey(category, key)
            .map(entityMapper::toDomain);
    }

    @Override
    public List<Configuration> findAll() {
        return jpaRepository.findAll().stream()
            .map(entityMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Configuration> findByCategory(ConfigCategory category) {
        return jpaRepository.findByCategory(category).stream()
            .map(entityMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public boolean existsByCategoryAndKey(ConfigCategory category, String key) {
        return jpaRepository.existsByCategoryAndKey(category, key);
    }
}
