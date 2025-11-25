package com.school.sms.configuration.infrastructure.persistence.adapter;

import com.school.sms.configuration.domain.model.Configuration;
import com.school.sms.configuration.domain.model.ConfigurationCategory;
import com.school.sms.configuration.domain.repository.ConfigurationRepository;
import com.school.sms.configuration.infrastructure.persistence.entity.ConfigurationJpaEntity;
import com.school.sms.configuration.infrastructure.persistence.repository.JpaConfigurationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adapter implementing domain repository interface using JPA
 */
@Component
@RequiredArgsConstructor
public class ConfigurationRepositoryAdapter implements ConfigurationRepository {

    private final JpaConfigurationRepository jpaRepository;

    @Override
    public Configuration save(Configuration configuration) {
        ConfigurationJpaEntity entity = toJpaEntity(configuration);
        ConfigurationJpaEntity saved = jpaRepository.save(entity);
        return toDomainModel(saved);
    }

    @Override
    public Optional<Configuration> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomainModel);
    }

    @Override
    public List<Configuration> findByCategory(ConfigurationCategory category) {
        return jpaRepository.findByCategory(category.name()).stream()
                .map(this::toDomainModel)
                .toList();
    }

    @Override
    public Page<Configuration> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable).map(this::toDomainModel);
    }

    @Override
    public boolean existsByCategoryAndConfigKey(ConfigurationCategory category, String configKey) {
        return jpaRepository.existsByCategoryAndConfigKey(category.name(), configKey);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    // Mapping methods
    private ConfigurationJpaEntity toJpaEntity(Configuration configuration) {
        return ConfigurationJpaEntity.builder()
                .id(configuration.getId())
                .category(configuration.getCategory().name())
                .configKey(configuration.getConfigKey())
                .configValue(configuration.getConfigValue())
                .description(configuration.getDescription())
                .createdAt(configuration.getCreatedAt())
                .updatedAt(configuration.getUpdatedAt())
                .createdBy(configuration.getCreatedBy())
                .updatedBy(configuration.getUpdatedBy())
                .version(configuration.getVersion())
                .build();
    }

    private Configuration toDomainModel(ConfigurationJpaEntity entity) {
        return Configuration.builder()
                .id(entity.getId())
                .category(ConfigurationCategory.valueOf(entity.getCategory()))
                .configKey(entity.getConfigKey())
                .configValue(entity.getConfigValue())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .version(entity.getVersion())
                .build();
    }
}
