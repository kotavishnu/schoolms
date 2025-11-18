package com.school.sms.configuration.infrastructure.persistence;

import com.school.sms.configuration.domain.entity.ConfigurationSetting;
import com.school.sms.configuration.domain.enums.SettingCategory;
import com.school.sms.configuration.domain.repository.ConfigurationSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adapter implementation of ConfigurationSettingRepository.
 * Delegates to JPA repository.
 */
@Component
@RequiredArgsConstructor
public class ConfigurationSettingRepositoryAdapter implements ConfigurationSettingRepository {

    private final JpaConfigurationSettingRepository jpaRepository;

    @Override
    public ConfigurationSetting save(ConfigurationSetting setting) {
        return jpaRepository.save(setting);
    }

    @Override
    public Optional<ConfigurationSetting> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<ConfigurationSetting> findByCategoryAndKey(SettingCategory category, String key) {
        return jpaRepository.findByCategoryAndKey(category, key);
    }

    @Override
    public List<ConfigurationSetting> findByCategory(SettingCategory category) {
        return jpaRepository.findByCategory(category);
    }

    @Override
    public List<ConfigurationSetting> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public boolean existsByCategoryAndKey(SettingCategory category, String key) {
        return jpaRepository.existsByCategoryAndKey(category, key);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }
}
