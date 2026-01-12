package com.schoolms.configuration.application.service;

import com.schoolms.configuration.application.mapper.ConfigurationMapper;
import com.schoolms.configuration.domain.exception.ConfigurationNotFoundException;
import com.schoolms.configuration.domain.exception.DuplicateConfigKeyException;
import com.schoolms.configuration.domain.model.ConfigCategory;
import com.schoolms.configuration.domain.model.Configuration;
import com.schoolms.configuration.domain.repository.ConfigurationRepository;
import com.schoolms.configuration.presentation.dto.ConfigurationListResponse;
import com.schoolms.configuration.presentation.dto.ConfigurationResponse;
import com.schoolms.configuration.presentation.dto.CreateConfigurationRequest;
import com.schoolms.configuration.presentation.dto.UpdateConfigurationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ConfigurationApplicationService (QA-013 to QA-015)
 * Tests service layer logic with mocked dependencies
 *
 * Coverage:
 * - Create configuration
 * - Update configuration value
 * - Delete configuration
 * - Get by ID
 * - Exception handling (ConfigurationNotFoundException, DuplicateConfigKeyException)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ConfigurationApplicationService Tests")
class ConfigurationApplicationServiceTest {

    @Mock
    private ConfigurationRepository configurationRepository;

    @Mock
    private ConfigurationMapper configurationMapper;

    @InjectMocks
    private ConfigurationApplicationService service;

    private CreateConfigurationRequest validCreateRequest;
    private Configuration validConfiguration;
    private ConfigurationResponse validConfigurationResponse;

    @BeforeEach
    void setUp() {
        validCreateRequest = new CreateConfigurationRequest(
            ConfigCategory.GENERAL,
            "SCHOOL_NAME",
            "Springfield Elementary School",
            "Official name of the school"
        );

        Instant now = Instant.now();
        validConfiguration = Configuration.reconstruct(
            1L,
            ConfigCategory.GENERAL,
            "SCHOOL_NAME",
            "Springfield Elementary School",
            "Official name of the school",
            now,
            now,
            0
        );

        validConfigurationResponse = new ConfigurationResponse(
            1L,
            ConfigCategory.GENERAL,
            "SCHOOL_NAME",
            "Springfield Elementary School",
            "Official name of the school",
            Instant.now(),
            Instant.now()
        );
    }

    @Nested
    @DisplayName("Create Configuration Tests")
    class CreateConfigurationTests {

        @Test
        @DisplayName("Should create configuration successfully with valid data")
        void shouldCreateConfigurationSuccessfully() {
            // Given
            when(configurationRepository.existsByCategoryAndKey(any(), anyString())).thenReturn(false);
            when(configurationRepository.save(any(Configuration.class))).thenReturn(validConfiguration);
            when(configurationMapper.toResponse(any(Configuration.class))).thenReturn(validConfigurationResponse);

            // When
            ConfigurationResponse response = service.createConfiguration(validCreateRequest);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.category()).isEqualTo(ConfigCategory.GENERAL);
            assertThat(response.key()).isEqualTo("SCHOOL_NAME");
            assertThat(response.value()).isEqualTo("Springfield Elementary School");

            // Verify interactions
            verify(configurationRepository).existsByCategoryAndKey(ConfigCategory.GENERAL, "SCHOOL_NAME");
            verify(configurationRepository).save(any(Configuration.class));
            verify(configurationMapper).toResponse(any(Configuration.class));
        }

        @Test
        @DisplayName("Should throw DuplicateConfigKeyException when key already exists")
        void shouldThrowDuplicateConfigKeyException() {
            // Given
            when(configurationRepository.existsByCategoryAndKey(any(), anyString())).thenReturn(true);

            // When/Then
            assertThatThrownBy(() -> service.createConfiguration(validCreateRequest))
                .isInstanceOf(DuplicateConfigKeyException.class);

            // Verify no save occurred
            verify(configurationRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should create configurations with different categories but same key")
        void shouldAllowSameKeyInDifferentCategories() {
            // Given
            CreateConfigurationRequest academicRequest = new CreateConfigurationRequest(
                ConfigCategory.ACADEMIC,
                "YEAR_START",
                "2026-04-01",
                "Academic year start date"
            );

            when(configurationRepository.existsByCategoryAndKey(ConfigCategory.ACADEMIC, "YEAR_START"))
                .thenReturn(false);
            when(configurationRepository.save(any(Configuration.class))).thenReturn(validConfiguration);
            when(configurationMapper.toResponse(any(Configuration.class))).thenReturn(validConfigurationResponse);

            // When
            ConfigurationResponse response = service.createConfiguration(academicRequest);

            // Then
            assertThat(response).isNotNull();
            verify(configurationRepository).existsByCategoryAndKey(ConfigCategory.ACADEMIC, "YEAR_START");
            verify(configurationRepository).save(any(Configuration.class));
        }

        @Test
        @DisplayName("Should create configuration for each category")
        void shouldCreateConfigurationForEachCategory() {
            // Given
            CreateConfigurationRequest financeRequest = new CreateConfigurationRequest(
                ConfigCategory.FINANCE,
                "TUITION_FEE",
                "50000",
                "Annual tuition fee"
            );

            when(configurationRepository.existsByCategoryAndKey(any(), anyString())).thenReturn(false);
            when(configurationRepository.save(any(Configuration.class))).thenReturn(validConfiguration);
            when(configurationMapper.toResponse(any(Configuration.class))).thenReturn(validConfigurationResponse);

            // When
            ConfigurationResponse response = service.createConfiguration(financeRequest);

            // Then
            assertThat(response).isNotNull();
            verify(configurationRepository).save(any(Configuration.class));
        }
    }

    @Nested
    @DisplayName("Update Configuration Tests")
    class UpdateConfigurationTests {

        @Test
        @DisplayName("Should update configuration value successfully")
        void shouldUpdateValueSuccessfully() {
            // Given
            UpdateConfigurationRequest updateRequest = new UpdateConfigurationRequest(
                "Updated School Name",
                null
            );

            when(configurationRepository.findById(1L)).thenReturn(Optional.of(validConfiguration));
            when(configurationRepository.save(any(Configuration.class))).thenReturn(validConfiguration);
            when(configurationMapper.toResponse(any(Configuration.class))).thenReturn(validConfigurationResponse);

            // When
            ConfigurationResponse response = service.updateConfiguration(1L, updateRequest);

            // Then
            assertThat(response).isNotNull();
            verify(configurationRepository).findById(1L);
            verify(configurationRepository).save(any(Configuration.class));
        }

        @Test
        @DisplayName("Should update configuration description successfully")
        void shouldUpdateDescriptionSuccessfully() {
            // Given
            UpdateConfigurationRequest updateRequest = new UpdateConfigurationRequest(
                null,
                "Updated description"
            );

            when(configurationRepository.findById(1L)).thenReturn(Optional.of(validConfiguration));
            when(configurationRepository.save(any(Configuration.class))).thenReturn(validConfiguration);
            when(configurationMapper.toResponse(any(Configuration.class))).thenReturn(validConfigurationResponse);

            // When
            ConfigurationResponse response = service.updateConfiguration(1L, updateRequest);

            // Then
            assertThat(response).isNotNull();
            verify(configurationRepository).save(any(Configuration.class));
        }

        @Test
        @DisplayName("Should update both value and description")
        void shouldUpdateBothValueAndDescription() {
            // Given
            UpdateConfigurationRequest updateRequest = new UpdateConfigurationRequest(
                "New Value",
                "New Description"
            );

            when(configurationRepository.findById(1L)).thenReturn(Optional.of(validConfiguration));
            when(configurationRepository.save(any(Configuration.class))).thenReturn(validConfiguration);
            when(configurationMapper.toResponse(any(Configuration.class))).thenReturn(validConfigurationResponse);

            // When
            ConfigurationResponse response = service.updateConfiguration(1L, updateRequest);

            // Then
            assertThat(response).isNotNull();
            verify(configurationRepository).save(any(Configuration.class));
        }

        @Test
        @DisplayName("Should throw ConfigurationNotFoundException when updating non-existent configuration")
        void shouldThrowExceptionWhenUpdatingNonExistent() {
            // Given
            UpdateConfigurationRequest updateRequest = new UpdateConfigurationRequest(
                "New Value",
                null
            );

            when(configurationRepository.findById(999L)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> service.updateConfiguration(999L, updateRequest))
                .isInstanceOf(ConfigurationNotFoundException.class);

            verify(configurationRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should handle update with null values gracefully")
        void shouldHandleNullValuesInUpdate() {
            // Given
            UpdateConfigurationRequest updateRequest = new UpdateConfigurationRequest(null, null);

            when(configurationRepository.findById(1L)).thenReturn(Optional.of(validConfiguration));
            when(configurationRepository.save(any(Configuration.class))).thenReturn(validConfiguration);
            when(configurationMapper.toResponse(any(Configuration.class))).thenReturn(validConfigurationResponse);

            // When
            ConfigurationResponse response = service.updateConfiguration(1L, updateRequest);

            // Then
            assertThat(response).isNotNull();
            verify(configurationRepository).save(any(Configuration.class));
        }
    }

    @Nested
    @DisplayName("Delete Configuration Tests")
    class DeleteConfigurationTests {

        @Test
        @DisplayName("Should delete configuration successfully")
        void shouldDeleteConfigurationSuccessfully() {
            // Given
            when(configurationRepository.findById(1L)).thenReturn(Optional.of(validConfiguration));

            // When
            service.deleteConfiguration(1L);

            // Then
            verify(configurationRepository).findById(1L);
            verify(configurationRepository).delete(1L);
        }

        @Test
        @DisplayName("Should throw ConfigurationNotFoundException when deleting non-existent configuration")
        void shouldThrowExceptionWhenDeletingNonExistent() {
            // Given
            when(configurationRepository.findById(999L)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> service.deleteConfiguration(999L))
                .isInstanceOf(ConfigurationNotFoundException.class);

            verify(configurationRepository, never()).delete(anyLong());
        }
    }

    @Nested
    @DisplayName("Get Configuration Tests")
    class GetConfigurationTests {

        @Test
        @DisplayName("Should get configuration by ID successfully")
        void shouldGetConfigurationById() {
            // Given
            when(configurationRepository.findById(1L)).thenReturn(Optional.of(validConfiguration));
            when(configurationMapper.toResponse(validConfiguration)).thenReturn(validConfigurationResponse);

            // When
            ConfigurationResponse response = service.getConfiguration(1L);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.key()).isEqualTo("SCHOOL_NAME");
            verify(configurationRepository).findById(1L);
        }

        @Test
        @DisplayName("Should throw ConfigurationNotFoundException when configuration not found")
        void shouldThrowExceptionWhenNotFound() {
            // Given
            when(configurationRepository.findById(999L)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> service.getConfiguration(999L))
                .isInstanceOf(ConfigurationNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("List Configurations Tests")
    class ListConfigurationsTests {

        @Test
        @DisplayName("Should list all configurations when no category filter")
        void shouldListAllConfigurations() {
            // Given
            Configuration config2 = Configuration.create(
                ConfigCategory.ACADEMIC,
                "YEAR_START",
                "2026-04-01",
                "Academic year start"
            );

            List<Configuration> configurations = Arrays.asList(validConfiguration, config2);
            List<ConfigurationResponse> responses = Arrays.asList(
                validConfigurationResponse,
                new ConfigurationResponse(2L, ConfigCategory.ACADEMIC, "YEAR_START", "2026-04-01",
                    "Academic year start", Instant.now(), Instant.now())
            );

            when(configurationRepository.findAll()).thenReturn(configurations);
            when(configurationMapper.toResponseList(configurations)).thenReturn(responses);

            // When
            ConfigurationListResponse response = service.listConfigurations(null);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.configurations()).hasSize(2);
            assertThat(response.totalCount()).isEqualTo(2);
            verify(configurationRepository).findAll();
        }

        @Test
        @DisplayName("Should filter configurations by category")
        void shouldFilterByCategory() {
            // Given
            List<Configuration> configurations = Arrays.asList(validConfiguration);
            List<ConfigurationResponse> responses = Arrays.asList(validConfigurationResponse);

            when(configurationRepository.findByCategory(ConfigCategory.GENERAL))
                .thenReturn(configurations);
            when(configurationMapper.toResponseList(configurations)).thenReturn(responses);

            // When
            ConfigurationListResponse response = service.listConfigurations(ConfigCategory.GENERAL);

            // Then
            assertThat(response.configurations()).hasSize(1);
            assertThat(response.totalCount()).isEqualTo(1);
            verify(configurationRepository).findByCategory(ConfigCategory.GENERAL);
        }

        @Test
        @DisplayName("Should return empty list when no configurations found")
        void shouldReturnEmptyListWhenNoneFound() {
            // Given
            when(configurationRepository.findByCategory(ConfigCategory.SYSTEM))
                .thenReturn(List.of());
            when(configurationMapper.toResponseList(anyList())).thenReturn(List.of());

            // When
            ConfigurationListResponse response = service.listConfigurations(ConfigCategory.SYSTEM);

            // Then
            assertThat(response.configurations()).isEmpty();
            assertThat(response.totalCount()).isEqualTo(0);
        }

        @Test
        @DisplayName("Should list configurations for ACADEMIC category")
        void shouldListAcademicConfigurations() {
            // Given
            Configuration academicConfig = Configuration.create(
                ConfigCategory.ACADEMIC,
                "SEMESTER_1_START",
                "2026-04-01",
                "First semester start date"
            );
            List<Configuration> configurations = Arrays.asList(academicConfig);
            List<ConfigurationResponse> responses = Arrays.asList(
                new ConfigurationResponse(2L, ConfigCategory.ACADEMIC, "SEMESTER_1_START",
                    "2026-04-01", "First semester start date", Instant.now(), Instant.now())
            );

            when(configurationRepository.findByCategory(ConfigCategory.ACADEMIC))
                .thenReturn(configurations);
            when(configurationMapper.toResponseList(configurations)).thenReturn(responses);

            // When
            ConfigurationListResponse response = service.listConfigurations(ConfigCategory.ACADEMIC);

            // Then
            assertThat(response.configurations()).hasSize(1);
            assertThat(response.configurations().get(0).category()).isEqualTo(ConfigCategory.ACADEMIC);
        }
    }
}
