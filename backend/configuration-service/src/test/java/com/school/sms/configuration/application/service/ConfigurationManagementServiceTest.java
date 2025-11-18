package com.school.sms.configuration.application.service;

import com.school.sms.configuration.application.dto.CreateSettingRequest;
import com.school.sms.configuration.application.dto.SettingResponse;
import com.school.sms.configuration.application.dto.UpdateSettingRequest;
import com.school.sms.configuration.application.mapper.ConfigurationMapper;
import com.school.sms.configuration.domain.entity.ConfigurationSetting;
import com.school.sms.configuration.domain.enums.SettingCategory;
import com.school.sms.configuration.domain.exception.DuplicateSettingException;
import com.school.sms.configuration.domain.exception.SettingNotFoundException;
import com.school.sms.configuration.domain.repository.ConfigurationSettingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ConfigurationManagementService (TDD approach).
 */
@ExtendWith(MockitoExtension.class)
class ConfigurationManagementServiceTest {

    @Mock
    private ConfigurationSettingRepository repository;

    @Mock
    private ConfigurationMapper mapper;

    @InjectMocks
    private ConfigurationManagementService service;

    private CreateSettingRequest createRequest;
    private ConfigurationSetting setting;
    private SettingResponse response;

    @BeforeEach
    void setUp() {
        createRequest = new CreateSettingRequest(
                SettingCategory.GENERAL,
                "SCHOOL_TIMEZONE",
                "Asia/Kolkata",
                "Default timezone"
        );

        setting = new ConfigurationSetting(
                SettingCategory.GENERAL,
                "SCHOOL_TIMEZONE",
                "Asia/Kolkata",
                "Default timezone"
        );

        response = SettingResponse.builder()
                .settingId(1L)
                .category(SettingCategory.GENERAL)
                .key("SCHOOL_TIMEZONE")
                .value("Asia/Kolkata")
                .description("Default timezone")
                .build();
    }

    @Test
    void shouldCreateSettingSuccessfully() {
        // given
        when(repository.existsByCategoryAndKey(SettingCategory.GENERAL, "SCHOOL_TIMEZONE"))
                .thenReturn(false);
        when(mapper.toEntity(createRequest)).thenReturn(setting);
        when(repository.save(any(ConfigurationSetting.class))).thenReturn(setting);
        when(mapper.toSettingResponse(setting)).thenReturn(response);

        // when
        SettingResponse result = service.createSetting(createRequest, "USER123");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getKey()).isEqualTo("SCHOOL_TIMEZONE");
        verify(repository).existsByCategoryAndKey(SettingCategory.GENERAL, "SCHOOL_TIMEZONE");
        verify(repository).save(any(ConfigurationSetting.class));
    }

    @Test
    void shouldThrowExceptionWhenCreatingDuplicateSetting() {
        // given
        when(repository.existsByCategoryAndKey(SettingCategory.GENERAL, "SCHOOL_TIMEZONE"))
                .thenReturn(true);

        // when/then
        assertThatThrownBy(() -> service.createSetting(createRequest, "USER123"))
                .isInstanceOf(DuplicateSettingException.class)
                .hasMessageContaining("GENERAL.SCHOOL_TIMEZONE");

        verify(repository, never()).save(any());
    }

    @Test
    void shouldGetSettingById() {
        // given
        when(repository.findById(1L)).thenReturn(Optional.of(setting));
        when(mapper.toSettingResponse(setting)).thenReturn(response);

        // when
        SettingResponse result = service.getSettingById(1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getSettingId()).isEqualTo(1L);
    }

    @Test
    void shouldThrowExceptionWhenSettingNotFoundById() {
        // given
        when(repository.findById(1L)).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> service.getSettingById(1L))
                .isInstanceOf(SettingNotFoundException.class);
    }

    @Test
    void shouldUpdateSettingSuccessfully() {
        // given
        UpdateSettingRequest updateRequest = new UpdateSettingRequest("New Value", "New Description", 0L);
        when(repository.findById(1L)).thenReturn(Optional.of(setting));
        when(repository.save(any(ConfigurationSetting.class))).thenReturn(setting);
        when(mapper.toSettingResponse(any(ConfigurationSetting.class))).thenReturn(response);

        // when
        SettingResponse result = service.updateSetting(1L, updateRequest, "USER123");

        // then
        assertThat(result).isNotNull();
        verify(repository).save(any(ConfigurationSetting.class));
    }

    @Test
    void shouldGetSettingsByCategory() {
        // given
        List<ConfigurationSetting> settings = List.of(setting);
        when(repository.findByCategory(SettingCategory.GENERAL)).thenReturn(settings);
        when(mapper.toSettingResponse(any(ConfigurationSetting.class))).thenReturn(response);

        // when
        var result = service.getSettingsByCategory(SettingCategory.GENERAL);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCategory()).isEqualTo(SettingCategory.GENERAL);
        assertThat(result.getSettings()).hasSize(1);
    }

    @Test
    void shouldGetAllSettingsGroupedByCategory() {
        // given
        List<ConfigurationSetting> settings = List.of(setting);
        when(repository.findAll()).thenReturn(settings);
        when(mapper.toSettingResponse(any(ConfigurationSetting.class))).thenReturn(response);

        // when
        Map<SettingCategory, List<SettingResponse>> result = service.getAllSettingsGroupedByCategory();

        // then
        assertThat(result).isNotNull();
        assertThat(result).containsKey(SettingCategory.GENERAL);
    }

    @Test
    void shouldDeleteSetting() {
        // given
        when(repository.findById(1L)).thenReturn(Optional.of(setting));

        // when
        service.deleteSetting(1L);

        // then
        verify(repository).deleteById(1L);
    }
}
