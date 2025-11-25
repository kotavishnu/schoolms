package com.school.sms.configuration.application.service;

import com.school.sms.configuration.application.dto.request.CreateConfigRequest;
import com.school.sms.configuration.application.dto.request.UpdateConfigRequest;
import com.school.sms.configuration.application.dto.response.ConfigurationPageResponse;
import com.school.sms.configuration.application.dto.response.ConfigurationResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Application service interface for configuration operations
 */
public interface ConfigurationService {

    ConfigurationResponse createConfiguration(CreateConfigRequest request);

    ConfigurationResponse getConfigurationById(Long id);

    List<ConfigurationResponse> getConfigurationsByCategory(String category);

    ConfigurationPageResponse getAllConfigurations(Pageable pageable);

    ConfigurationResponse updateConfiguration(Long id, UpdateConfigRequest request);

    void deleteConfiguration(Long id);
}
