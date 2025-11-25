package com.school.sms.configuration.infrastructure.persistence.repository;

import com.school.sms.configuration.infrastructure.persistence.entity.ConfigurationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaConfigurationRepository extends JpaRepository<ConfigurationJpaEntity, Long> {

    List<ConfigurationJpaEntity> findByCategory(String category);

    boolean existsByCategoryAndConfigKey(String category, String configKey);
}
