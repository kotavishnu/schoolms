package com.school.management.service;

import com.school.management.dto.request.SchoolConfigRequestDTO;
import com.school.management.dto.response.SchoolConfigResponseDTO;
import com.school.management.exception.DuplicateResourceException;
import com.school.management.exception.ResourceNotFoundException;
import com.school.management.exception.ValidationException;
import com.school.management.mapper.SchoolConfigMapper;
import com.school.management.model.SchoolConfig;
import com.school.management.repository.SchoolConfigRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SchoolConfigService
 *
 * Uses Mockito to mock dependencies and test business logic.
 *
 * @author School Management Team
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SchoolConfigService Tests")
class SchoolConfigServiceTest {

    @Mock
    private SchoolConfigRepository schoolConfigRepository;

    @Mock
    private SchoolConfigMapper schoolConfigMapper;

    @InjectMocks
    private SchoolConfigService schoolConfigService;

    private SchoolConfigRequestDTO requestDTO;
    private SchoolConfig schoolConfig;
    private SchoolConfigResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        // Setup test data
        requestDTO = SchoolConfigRequestDTO.builder()
                .configKey("SCHOOL_NAME")
                .configValue("XYZ International School")
                .category("GENERAL")
                .description("School name configuration")
                .isEditable(true)
                .dataType("STRING")
                .build();

        schoolConfig = SchoolConfig.builder()
                .id(1L)
                .configKey("SCHOOL_NAME")
                .configValue("XYZ International School")
                .category("GENERAL")
                .description("School name configuration")
                .isEditable(true)
                .dataType("STRING")
                .build();

        responseDTO = SchoolConfigResponseDTO.builder()
                .id(1L)
                .configKey("SCHOOL_NAME")
                .configValue("XYZ International School")
                .category("GENERAL")
                .description("School name configuration")
                .isEditable(true)
                .dataType("STRING")
                .build();
    }

    @Test
    @DisplayName("Should create school config successfully")
    void shouldCreateSchoolConfigSuccessfully() {
        // Given
        when(schoolConfigRepository.existsByConfigKey("SCHOOL_NAME")).thenReturn(false);
        when(schoolConfigMapper.toEntity(any(SchoolConfigRequestDTO.class))).thenReturn(schoolConfig);
        when(schoolConfigRepository.save(any(SchoolConfig.class))).thenReturn(schoolConfig);
        when(schoolConfigMapper.toResponseDTO(any(SchoolConfig.class))).thenReturn(responseDTO);

        // When
        SchoolConfigResponseDTO result = schoolConfigService.createConfig(requestDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getConfigKey()).isEqualTo("SCHOOL_NAME");
        assertThat(result.getConfigValue()).isEqualTo("XYZ International School");

        verify(schoolConfigRepository).existsByConfigKey("SCHOOL_NAME");
        verify(schoolConfigRepository).save(any(SchoolConfig.class));
    }

    @Test
    @DisplayName("Should throw exception when duplicate config key exists")
    void shouldThrowExceptionWhenDuplicateConfigKey() {
        // Given
        when(schoolConfigRepository.existsByConfigKey("SCHOOL_NAME")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> schoolConfigService.createConfig(requestDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("SchoolConfig")
                .hasMessageContaining("SCHOOL_NAME");

        verify(schoolConfigRepository).existsByConfigKey("SCHOOL_NAME");
        verify(schoolConfigRepository, never()).save(any(SchoolConfig.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"STRING", "INTEGER", "BOOLEAN", "JSON"})
    @DisplayName("Should create config with different data types")
    void shouldCreateConfigWithDifferentDataTypes(String dataType) {
        // Given
        SchoolConfigRequestDTO configDTO = SchoolConfigRequestDTO.builder()
                .configKey("TEST_CONFIG_" + dataType)
                .configValue("Test Value")
                .category("TEST")
                .isEditable(true)
                .dataType(dataType)
                .build();

        SchoolConfig entity = SchoolConfig.builder()
                .id(1L)
                .configKey("TEST_CONFIG_" + dataType)
                .configValue("Test Value")
                .dataType(dataType)
                .build();

        SchoolConfigResponseDTO response = SchoolConfigResponseDTO.builder()
                .id(1L)
                .configKey("TEST_CONFIG_" + dataType)
                .configValue("Test Value")
                .dataType(dataType)
                .build();

        when(schoolConfigRepository.existsByConfigKey(anyString())).thenReturn(false);
        when(schoolConfigMapper.toEntity(any(SchoolConfigRequestDTO.class))).thenReturn(entity);
        when(schoolConfigRepository.save(any(SchoolConfig.class))).thenReturn(entity);
        when(schoolConfigMapper.toResponseDTO(any(SchoolConfig.class))).thenReturn(response);

        // When
        SchoolConfigResponseDTO result = schoolConfigService.createConfig(configDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getDataType()).isEqualTo(dataType);

        verify(schoolConfigRepository).save(any(SchoolConfig.class));
    }

    @Test
    @DisplayName("Should get config by ID")
    void shouldGetConfigById() {
        // Given
        when(schoolConfigRepository.findById(1L)).thenReturn(Optional.of(schoolConfig));
        when(schoolConfigMapper.toResponseDTO(any(SchoolConfig.class))).thenReturn(responseDTO);

        // When
        SchoolConfigResponseDTO result = schoolConfigService.getConfig(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);

        verify(schoolConfigRepository).findById(1L);
    }

    @Test
    @DisplayName("Should get config by key")
    void shouldGetConfigByKey() {
        // Given
        when(schoolConfigRepository.findByConfigKey("SCHOOL_NAME")).thenReturn(Optional.of(schoolConfig));
        when(schoolConfigMapper.toResponseDTO(any(SchoolConfig.class))).thenReturn(responseDTO);

        // When
        SchoolConfigResponseDTO result = schoolConfigService.getConfigByKey("SCHOOL_NAME");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getConfigKey()).isEqualTo("SCHOOL_NAME");

        verify(schoolConfigRepository).findByConfigKey("SCHOOL_NAME");
    }

    @Test
    @DisplayName("Should get config value only")
    void shouldGetConfigValueOnly() {
        // Given
        when(schoolConfigRepository.findByConfigKey("SCHOOL_NAME")).thenReturn(Optional.of(schoolConfig));

        // When
        String value = schoolConfigService.getConfigValue("SCHOOL_NAME");

        // Then
        assertThat(value).isEqualTo("XYZ International School");

        verify(schoolConfigRepository).findByConfigKey("SCHOOL_NAME");
    }

    @Test
    @DisplayName("Should get all configs")
    void shouldGetAllConfigs() {
        // Given
        List<SchoolConfig> configs = new ArrayList<>();
        configs.add(schoolConfig);

        List<SchoolConfigResponseDTO> responseDTOs = new ArrayList<>();
        responseDTOs.add(responseDTO);

        when(schoolConfigRepository.findAll()).thenReturn(configs);
        when(schoolConfigMapper.toResponseDTOList(anyList())).thenReturn(responseDTOs);

        // When
        List<SchoolConfigResponseDTO> result = schoolConfigService.getAllConfigs();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);

        verify(schoolConfigRepository).findAll();
    }

    @Test
    @DisplayName("Should get configs by category")
    void shouldGetConfigsByCategory() {
        // Given
        List<SchoolConfig> configs = new ArrayList<>();
        configs.add(schoolConfig);

        List<SchoolConfigResponseDTO> responseDTOs = new ArrayList<>();
        responseDTOs.add(responseDTO);

        when(schoolConfigRepository.findByCategory("GENERAL")).thenReturn(configs);
        when(schoolConfigMapper.toResponseDTOList(anyList())).thenReturn(responseDTOs);

        // When
        List<SchoolConfigResponseDTO> result = schoolConfigService.getConfigsByCategory("GENERAL");

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);

        verify(schoolConfigRepository).findByCategory("GENERAL");
    }

    @Test
    @DisplayName("Should get editable configs only")
    void shouldGetEditableConfigsOnly() {
        // Given
        List<SchoolConfig> configs = new ArrayList<>();
        configs.add(schoolConfig);

        List<SchoolConfigResponseDTO> responseDTOs = new ArrayList<>();
        responseDTOs.add(responseDTO);

        when(schoolConfigRepository.findByIsEditable(true)).thenReturn(configs);
        when(schoolConfigMapper.toResponseDTOList(anyList())).thenReturn(responseDTOs);

        // When
        List<SchoolConfigResponseDTO> result = schoolConfigService.getEditableConfigs();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);

        verify(schoolConfigRepository).findByIsEditable(true);
    }

    @Test
    @DisplayName("Should check if config exists by key")
    void shouldCheckIfConfigExistsByKey() {
        // Given
        when(schoolConfigRepository.existsByConfigKey("SCHOOL_NAME")).thenReturn(true);

        // When
        boolean exists = schoolConfigService.configExists("SCHOOL_NAME");

        // Then
        assertThat(exists).isTrue();

        verify(schoolConfigRepository).existsByConfigKey("SCHOOL_NAME");
    }

    @Test
    @DisplayName("Should update config value")
    void shouldUpdateConfigValue() {
        // Given
        when(schoolConfigRepository.findByConfigKey("SCHOOL_NAME")).thenReturn(Optional.of(schoolConfig));
        when(schoolConfigRepository.save(any(SchoolConfig.class))).thenReturn(schoolConfig);
        when(schoolConfigMapper.toResponseDTO(any(SchoolConfig.class))).thenReturn(responseDTO);

        // When
        SchoolConfigResponseDTO result = schoolConfigService.updateConfigValue("SCHOOL_NAME", "New School Name");

        // Then
        assertThat(result).isNotNull();

        verify(schoolConfigRepository).findByConfigKey("SCHOOL_NAME");
        verify(schoolConfigRepository).save(schoolConfig);
        assertThat(schoolConfig.getConfigValue()).isEqualTo("New School Name");
    }

    @Test
    @DisplayName("Should throw exception when updating non-editable config")
    void shouldThrowExceptionWhenUpdatingNonEditableConfig() {
        // Given
        SchoolConfig systemConfig = SchoolConfig.builder()
                .id(1L)
                .configKey("SYSTEM_VERSION")
                .configValue("1.0.0")
                .isEditable(false) // Non-editable
                .build();

        when(schoolConfigRepository.findByConfigKey("SYSTEM_VERSION")).thenReturn(Optional.of(systemConfig));

        // When & Then
        assertThatThrownBy(() -> schoolConfigService.updateConfigValue("SYSTEM_VERSION", "2.0.0"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Cannot update system configuration");

        verify(schoolConfigRepository).findByConfigKey("SYSTEM_VERSION");
        verify(schoolConfigRepository, never()).save(any(SchoolConfig.class));
    }

    @Test
    @DisplayName("Should delete config successfully")
    void shouldDeleteConfigSuccessfully() {
        // Given
        when(schoolConfigRepository.findById(1L)).thenReturn(Optional.of(schoolConfig));

        // When
        schoolConfigService.deleteConfig(1L);

        // Then
        verify(schoolConfigRepository).findById(1L);
        verify(schoolConfigRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-editable config")
    void shouldThrowExceptionWhenDeletingNonEditableConfig() {
        // Given
        SchoolConfig systemConfig = SchoolConfig.builder()
                .id(1L)
                .configKey("SYSTEM_VERSION")
                .configValue("1.0.0")
                .isEditable(false) // Non-editable
                .build();

        when(schoolConfigRepository.findById(1L)).thenReturn(Optional.of(systemConfig));

        // When & Then
        assertThatThrownBy(() -> schoolConfigService.deleteConfig(1L))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Cannot delete system configuration");

        verify(schoolConfigRepository).findById(1L);
        verify(schoolConfigRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should update config successfully")
    void shouldUpdateConfigSuccessfully() {
        // Given
        SchoolConfigRequestDTO updateDTO = SchoolConfigRequestDTO.builder()
                .configKey("SCHOOL_NAME")
                .configValue("Updated School Name")
                .category("GENERAL")
                .isEditable(true)
                .dataType("STRING")
                .build();

        when(schoolConfigRepository.findById(1L)).thenReturn(Optional.of(schoolConfig));
        when(schoolConfigRepository.findByConfigKey("SCHOOL_NAME")).thenReturn(Optional.empty());
        when(schoolConfigRepository.save(any(SchoolConfig.class))).thenReturn(schoolConfig);
        when(schoolConfigMapper.toResponseDTO(any(SchoolConfig.class))).thenReturn(responseDTO);
        doNothing().when(schoolConfigMapper).updateEntityFromDTO(any(SchoolConfigRequestDTO.class), any(SchoolConfig.class));

        // When
        SchoolConfigResponseDTO result = schoolConfigService.updateConfig(1L, updateDTO);

        // Then
        assertThat(result).isNotNull();

        verify(schoolConfigRepository).findById(1L);
        verify(schoolConfigMapper).updateEntityFromDTO(updateDTO, schoolConfig);
        verify(schoolConfigRepository).save(schoolConfig);
    }
}
