package com.school.sms.configuration.infrastructure.persistence;

import com.school.sms.configuration.domain.entity.ConfigurationSetting;
import com.school.sms.configuration.domain.enums.SettingCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA repository for ConfigurationSetting entity.
 */
@Repository
public interface JpaConfigurationSettingRepository extends JpaRepository<ConfigurationSetting, Long> {

    /**
     * Find setting by category and key.
     *
     * @param category the category
     * @param key      the key
     * @return optional containing the setting
     */
    Optional<ConfigurationSetting> findByCategoryAndKey(SettingCategory category, String key);

    /**
     * Find all settings by category.
     *
     * @param category the category
     * @return list of settings
     */
    List<ConfigurationSetting> findByCategory(SettingCategory category);

    /**
     * Check if setting exists by category and key.
     *
     * @param category the category
     * @param key      the key
     * @return true if exists
     */
    boolean existsByCategoryAndKey(SettingCategory category, String key);
}
