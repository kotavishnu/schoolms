package com.school.configuration.infrastructure.persistence;

import com.school.configuration.domain.model.ConfigCategory;
import com.school.configuration.domain.model.Configuration;
import com.school.configuration.domain.repository.ConfigurationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of ConfigurationRepository using Spring Data JPA.
 * Acts as adapter between domain and infrastructure layers.
 * Handles conversion between domain models and JPA entities.
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class ConfigurationRepositoryImpl implements ConfigurationRepository {

    private final ConfigurationJpaRepository jpaRepository;
    private final ConfigurationEntityMapper entityMapper;

    @Override
    public Configuration save(Configuration configuration) {
        log.debug("Saving configuration: category={}, key={}",
            configuration.getCategory(), configuration.getKey());

        ConfigurationEntity entity = entityMapper.toEntity(configuration);
        ConfigurationEntity savedEntity = jpaRepository.save(entity);

        log.debug("Configuration saved with ID: {}", savedEntity.getId());
        return entityMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Configuration> findById(Long id) {
        log.debug("Finding configuration by ID: {}", id);

        return jpaRepository.findById(id)
            .map(entityMapper::toDomain);
    }

    @Override
    public Optional<Configuration> findByCategoryAndKey(ConfigCategory category, String key) {
        log.debug("Finding configuration by category={} and key={}", category, key);

        String categoryString = category.name();
        return jpaRepository.findByCategoryAndKey(categoryString, key)
            .map(entityMapper::toDomain);
    }

    @Override
    public List<Configuration> findByCategory(ConfigCategory category) {
        log.debug("Finding all configurations by category={}", category);

        String categoryString = category.name();
        List<ConfigurationEntity> entities = jpaRepository.findByCategory(categoryString);

        log.debug("Found {} configurations for category={}", entities.size(), category);
        return entityMapper.toDomainList(entities);
    }

    @Override
    public List<Configuration> findAll() {
        log.debug("Finding all configurations");

        List<ConfigurationEntity> entities = jpaRepository.findAll();

        log.debug("Found {} configurations", entities.size());
        return entityMapper.toDomainList(entities);
    }

    @Override
    public void delete(Configuration configuration) {
        log.debug("Deleting configuration: category={}, key={}",
            configuration.getCategory(), configuration.getKey());

        jpaRepository.deleteById(configuration.getId());

        log.debug("Configuration deleted successfully");
    }

    @Override
    public boolean existsByCategoryAndKey(ConfigCategory category, String key) {
        log.debug("Checking if configuration exists: category={}, key={}", category, key);

        String categoryString = category.name();
        boolean exists = jpaRepository.existsByCategoryAndKey(categoryString, key);

        log.debug("Configuration exists: {}", exists);
        return exists;
    }
}
