package com.school.management.service;

import com.school.management.dto.request.FeeMasterRequestDTO;
import com.school.management.dto.response.FeeMasterResponseDTO;
import com.school.management.exception.ResourceNotFoundException;
import com.school.management.exception.ValidationException;
import com.school.management.mapper.FeeMasterMapper;
import com.school.management.model.FeeMaster;
import com.school.management.model.FeeType;
import com.school.management.repository.FeeMasterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for FeeMasterService
 *
 * Uses Mockito to mock dependencies and test business logic.
 *
 * @author School Management Team
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FeeMasterService Tests")
class FeeMasterServiceTest {

    @Mock
    private FeeMasterRepository feeMasterRepository;

    @Mock
    private FeeMasterMapper feeMasterMapper;

    @InjectMocks
    private FeeMasterService feeMasterService;

    private FeeMasterRequestDTO requestDTO;
    private FeeMaster feeMaster;
    private FeeMasterResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        // Setup test data
        requestDTO = FeeMasterRequestDTO.builder()
                .feeType(FeeType.TUITION)
                .amount(new BigDecimal("5000.00"))
                .frequency("MONTHLY")
                .applicableFrom(LocalDate.of(2024, 1, 1))
                .applicableTo(LocalDate.of(2024, 12, 31))
                .academicYear("2024-2025")
                .description("Monthly tuition fee for 2024-2025")
                .isActive(true)
                .build();

        feeMaster = FeeMaster.builder()
                .id(1L)
                .feeType(FeeType.TUITION)
                .amount(new BigDecimal("5000.00"))
                .frequency("MONTHLY")
                .applicableFrom(LocalDate.of(2024, 1, 1))
                .applicableTo(LocalDate.of(2024, 12, 31))
                .academicYear("2024-2025")
                .description("Monthly tuition fee for 2024-2025")
                .isActive(true)
                .build();

        responseDTO = FeeMasterResponseDTO.builder()
                .id(1L)
                .feeType(FeeType.TUITION)
                .amount(new BigDecimal("5000.00"))
                .frequency("MONTHLY")
                .applicableFrom(LocalDate.of(2024, 1, 1))
                .applicableTo(LocalDate.of(2024, 12, 31))
                .academicYear("2024-2025")
                .description("Monthly tuition fee for 2024-2025")
                .isActive(true)
                .build();
    }

    @Test
    @DisplayName("Should create fee master successfully")
    void shouldCreateFeeMasterSuccessfully() {
        // Given
        when(feeMasterMapper.toEntity(any(FeeMasterRequestDTO.class))).thenReturn(feeMaster);
        when(feeMasterRepository.save(any(FeeMaster.class))).thenReturn(feeMaster);
        when(feeMasterMapper.toResponseDTO(any(FeeMaster.class))).thenReturn(responseDTO);

        // When
        FeeMasterResponseDTO result = feeMasterService.createFeeMaster(requestDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getFeeType()).isEqualTo(FeeType.TUITION);
        assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("5000.00"));

        verify(feeMasterMapper).toEntity(requestDTO);
        verify(feeMasterRepository).save(any(FeeMaster.class));
    }

    @Test
    @DisplayName("Should throw exception when applicableTo is before applicableFrom")
    void shouldThrowExceptionWhenInvalidDateRange() {
        // Given
        FeeMasterRequestDTO invalidDTO = FeeMasterRequestDTO.builder()
                .feeType(FeeType.TUITION)
                .amount(new BigDecimal("5000.00"))
                .frequency("MONTHLY")
                .applicableFrom(LocalDate.of(2024, 12, 31))
                .applicableTo(LocalDate.of(2024, 1, 1)) // Before applicableFrom
                .academicYear("2024-2025")
                .isActive(true)
                .build();

        // When & Then
        assertThatThrownBy(() -> feeMasterService.createFeeMaster(invalidDTO))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Applicable to date must be after applicable from date");

        verify(feeMasterRepository, never()).save(any(FeeMaster.class));
    }

    @ParameterizedTest
    @EnumSource(FeeType.class)
    @DisplayName("Should create fee master for all fee types")
    void shouldCreateFeeMasterForAllFeeTypes(FeeType feeType) {
        // Given
        FeeMasterRequestDTO feeDTO = FeeMasterRequestDTO.builder()
                .feeType(feeType)
                .amount(new BigDecimal("1000.00"))
                .frequency("MONTHLY")
                .applicableFrom(LocalDate.of(2024, 1, 1))
                .applicableTo(LocalDate.of(2024, 12, 31))
                .academicYear("2024-2025")
                .isActive(true)
                .build();

        FeeMaster entity = FeeMaster.builder()
                .id(1L)
                .feeType(feeType)
                .amount(new BigDecimal("1000.00"))
                .build();

        FeeMasterResponseDTO response = FeeMasterResponseDTO.builder()
                .id(1L)
                .feeType(feeType)
                .amount(new BigDecimal("1000.00"))
                .build();

        when(feeMasterMapper.toEntity(any(FeeMasterRequestDTO.class))).thenReturn(entity);
        when(feeMasterRepository.save(any(FeeMaster.class))).thenReturn(entity);
        when(feeMasterMapper.toResponseDTO(any(FeeMaster.class))).thenReturn(response);

        // When
        FeeMasterResponseDTO result = feeMasterService.createFeeMaster(feeDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getFeeType()).isEqualTo(feeType);

        verify(feeMasterRepository).save(any(FeeMaster.class));
    }

    @Test
    @DisplayName("Should get fee master by ID")
    void shouldGetFeeMasterById() {
        // Given
        when(feeMasterRepository.findById(1L)).thenReturn(Optional.of(feeMaster));
        when(feeMasterMapper.toResponseDTO(any(FeeMaster.class))).thenReturn(responseDTO);

        // When
        FeeMasterResponseDTO result = feeMasterService.getFeeMaster(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);

        verify(feeMasterRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when fee master not found")
    void shouldThrowExceptionWhenFeeMasterNotFound() {
        // Given
        when(feeMasterRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> feeMasterService.getFeeMaster(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("FeeMaster")
                .hasMessageContaining("999");

        verify(feeMasterRepository).findById(999L);
    }

    @Test
    @DisplayName("Should get all fee masters")
    void shouldGetAllFeeMasters() {
        // Given
        List<FeeMaster> feeMasters = new ArrayList<>();
        feeMasters.add(feeMaster);

        List<FeeMasterResponseDTO> responseDTOs = new ArrayList<>();
        responseDTOs.add(responseDTO);

        when(feeMasterRepository.findAll()).thenReturn(feeMasters);
        when(feeMasterMapper.toResponseDTOList(anyList())).thenReturn(responseDTOs);

        // When
        List<FeeMasterResponseDTO> result = feeMasterService.getAllFeeMasters();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);

        verify(feeMasterRepository).findAll();
    }

    @Test
    @DisplayName("Should get fee masters by academic year")
    void shouldGetFeeMastersByAcademicYear() {
        // Given
        List<FeeMaster> feeMasters = new ArrayList<>();
        feeMasters.add(feeMaster);

        List<FeeMasterResponseDTO> responseDTOs = new ArrayList<>();
        responseDTOs.add(responseDTO);

        when(feeMasterRepository.findByAcademicYear("2024-2025")).thenReturn(feeMasters);
        when(feeMasterMapper.toResponseDTOList(anyList())).thenReturn(responseDTOs);

        // When
        List<FeeMasterResponseDTO> result = feeMasterService.getFeeMastersByAcademicYear("2024-2025");

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);

        verify(feeMasterRepository).findByAcademicYear("2024-2025");
    }

    @Test
    @DisplayName("Should get fee masters by type")
    void shouldGetFeeMastersByType() {
        // Given
        List<FeeMaster> feeMasters = new ArrayList<>();
        feeMasters.add(feeMaster);

        List<FeeMasterResponseDTO> responseDTOs = new ArrayList<>();
        responseDTOs.add(responseDTO);

        when(feeMasterRepository.findByFeeType(FeeType.TUITION)).thenReturn(feeMasters);
        when(feeMasterMapper.toResponseDTOList(anyList())).thenReturn(responseDTOs);

        // When
        List<FeeMasterResponseDTO> result = feeMasterService.getFeeMastersByType(FeeType.TUITION);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFeeType()).isEqualTo(FeeType.TUITION);

        verify(feeMasterRepository).findByFeeType(FeeType.TUITION);
    }

    @Test
    @DisplayName("Should get active fee masters only")
    void shouldGetActiveFeeMastersOnly() {
        // Given
        List<FeeMaster> feeMasters = new ArrayList<>();
        feeMasters.add(feeMaster);

        List<FeeMasterResponseDTO> responseDTOs = new ArrayList<>();
        responseDTOs.add(responseDTO);

        when(feeMasterRepository.findByIsActiveTrue()).thenReturn(feeMasters);
        when(feeMasterMapper.toResponseDTOList(anyList())).thenReturn(responseDTOs);

        // When
        List<FeeMasterResponseDTO> result = feeMasterService.getActiveFeeMasters();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);

        verify(feeMasterRepository).findByIsActiveTrue();
    }

    @Test
    @DisplayName("Should get currently applicable fee masters")
    void shouldGetCurrentlyApplicableFeeMasters() {
        // Given
        List<FeeMaster> feeMasters = new ArrayList<>();
        feeMasters.add(feeMaster);

        List<FeeMasterResponseDTO> responseDTOs = new ArrayList<>();
        responseDTOs.add(responseDTO);

        when(feeMasterRepository.findCurrentlyApplicable(any(LocalDate.class))).thenReturn(feeMasters);
        when(feeMasterMapper.toResponseDTOList(anyList())).thenReturn(responseDTOs);

        // When
        List<FeeMasterResponseDTO> result = feeMasterService.getCurrentlyApplicableFeeMasters();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);

        verify(feeMasterRepository).findCurrentlyApplicable(any(LocalDate.class));
    }

    @Test
    @DisplayName("Should get latest fee master by type")
    void shouldGetLatestFeeMasterByType() {
        // Given
        when(feeMasterRepository.findLatestApplicable(eq(FeeType.TUITION), any(LocalDate.class)))
                .thenReturn(Optional.of(feeMaster));
        when(feeMasterMapper.toResponseDTO(any(FeeMaster.class))).thenReturn(responseDTO);

        // When
        FeeMasterResponseDTO result = feeMasterService.getLatestApplicableFeeMaster(FeeType.TUITION);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getFeeType()).isEqualTo(FeeType.TUITION);

        verify(feeMasterRepository).findLatestApplicable(eq(FeeType.TUITION), any(LocalDate.class));
    }

    @Test
    @DisplayName("Should update fee master successfully")
    void shouldUpdateFeeMasterSuccessfully() {
        // Given
        FeeMasterRequestDTO updateDTO = FeeMasterRequestDTO.builder()
                .feeType(FeeType.TUITION)
                .amount(new BigDecimal("6000.00")) // Updated amount
                .frequency("MONTHLY")
                .applicableFrom(LocalDate.of(2024, 1, 1))
                .applicableTo(LocalDate.of(2024, 12, 31))
                .academicYear("2024-2025")
                .isActive(true)
                .build();

        when(feeMasterRepository.findById(1L)).thenReturn(Optional.of(feeMaster));
        when(feeMasterRepository.save(any(FeeMaster.class))).thenReturn(feeMaster);
        when(feeMasterMapper.toResponseDTO(any(FeeMaster.class))).thenReturn(responseDTO);
        doNothing().when(feeMasterMapper).updateEntityFromDTO(any(FeeMasterRequestDTO.class), any(FeeMaster.class));

        // When
        FeeMasterResponseDTO result = feeMasterService.updateFeeMaster(1L, updateDTO);

        // Then
        assertThat(result).isNotNull();

        verify(feeMasterRepository).findById(1L);
        verify(feeMasterMapper).updateEntityFromDTO(updateDTO, feeMaster);
        verify(feeMasterRepository).save(feeMaster);
    }

    @Test
    @DisplayName("Should activate fee master")
    void shouldActivateFeeMaster() {
        // Given
        feeMaster.setIsActive(false);

        when(feeMasterRepository.findById(1L)).thenReturn(Optional.of(feeMaster));
        when(feeMasterRepository.save(any(FeeMaster.class))).thenReturn(feeMaster);
        when(feeMasterMapper.toResponseDTO(any(FeeMaster.class))).thenReturn(responseDTO);

        // When
        FeeMasterResponseDTO result = feeMasterService.activateFeeMaster(1L);

        // Then
        assertThat(result).isNotNull();

        verify(feeMasterRepository).findById(1L);
        verify(feeMasterRepository).save(feeMaster);
        assertThat(feeMaster.getIsActive()).isTrue();
    }

    @Test
    @DisplayName("Should deactivate fee master")
    void shouldDeactivateFeeMaster() {
        // Given
        when(feeMasterRepository.findById(1L)).thenReturn(Optional.of(feeMaster));
        when(feeMasterRepository.save(any(FeeMaster.class))).thenReturn(feeMaster);
        when(feeMasterMapper.toResponseDTO(any(FeeMaster.class))).thenReturn(responseDTO);

        // When
        FeeMasterResponseDTO result = feeMasterService.deactivateFeeMaster(1L);

        // Then
        assertThat(result).isNotNull();

        verify(feeMasterRepository).findById(1L);
        verify(feeMasterRepository).save(feeMaster);
        assertThat(feeMaster.getIsActive()).isFalse();
    }

    @Test
    @DisplayName("Should delete fee master successfully")
    void shouldDeleteFeeMasterSuccessfully() {
        // Given
        when(feeMasterRepository.findById(1L)).thenReturn(Optional.of(feeMaster));

        // When
        feeMasterService.deleteFeeMaster(1L);

        // Then
        verify(feeMasterRepository).findById(1L);
        verify(feeMasterRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should get active fee masters by type")
    void shouldGetActiveFeeMastersByType() {
        // Given
        List<FeeMaster> feeMasters = new ArrayList<>();
        feeMasters.add(feeMaster);

        List<FeeMasterResponseDTO> responseDTOs = new ArrayList<>();
        responseDTOs.add(responseDTO);

        when(feeMasterRepository.findByFeeTypeAndIsActive(FeeType.TUITION, true)).thenReturn(feeMasters);
        when(feeMasterMapper.toResponseDTOList(anyList())).thenReturn(responseDTOs);

        // When
        List<FeeMasterResponseDTO> result = feeMasterService.getActiveFeeMastersByType(FeeType.TUITION);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);

        verify(feeMasterRepository).findByFeeTypeAndIsActive(FeeType.TUITION, true);
    }
}
