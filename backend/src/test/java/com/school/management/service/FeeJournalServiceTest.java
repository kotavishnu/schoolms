package com.school.management.service;

import com.school.management.dto.request.FeeJournalRequestDTO;
import com.school.management.dto.response.FeeJournalResponseDTO;
import com.school.management.exception.DuplicateResourceException;
import com.school.management.exception.ResourceNotFoundException;
import com.school.management.exception.ValidationException;
import com.school.management.mapper.FeeJournalMapper;
import com.school.management.model.FeeJournal;
import com.school.management.model.PaymentStatus;
import com.school.management.model.Student;
import com.school.management.repository.FeeJournalRepository;
import com.school.management.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
 * Unit tests for FeeJournalService
 *
 * Uses Mockito to mock dependencies and test business logic.
 *
 * @author School Management Team
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FeeJournalService Tests")
class FeeJournalServiceTest {

    @Mock
    private FeeJournalRepository feeJournalRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private FeeJournalMapper feeJournalMapper;

    @InjectMocks
    private FeeJournalService feeJournalService;

    private FeeJournalRequestDTO requestDTO;
    private FeeJournal feeJournal;
    private FeeJournalResponseDTO responseDTO;
    private Student student;

    @BeforeEach
    void setUp() {
        // Setup student
        student = Student.builder()
                .id(1L)
                .firstName("Rajesh")
                .lastName("Kumar")
                .build();

        // Setup test data
        requestDTO = FeeJournalRequestDTO.builder()
                .studentId(1L)
                .month("January")
                .year(2024)
                .amountDue(new BigDecimal("5000.00"))
                .amountPaid(BigDecimal.ZERO)
                .dueDate(LocalDate.of(2024, 1, 10))
                .remarks("Monthly fee")
                .build();

        feeJournal = FeeJournal.builder()
                .id(1L)
                .student(student)
                .month("January")
                .year(2024)
                .amountDue(new BigDecimal("5000.00"))
                .amountPaid(BigDecimal.ZERO)
                .balance(new BigDecimal("5000.00"))
                .status(PaymentStatus.PENDING)
                .dueDate(LocalDate.of(2024, 1, 10))
                .remarks("Monthly fee")
                .build();

        responseDTO = FeeJournalResponseDTO.builder()
                .id(1L)
                .studentId(1L)
                .studentName("Rajesh Kumar")
                .month("January")
                .year(2024)
                .amountDue(new BigDecimal("5000.00"))
                .amountPaid(BigDecimal.ZERO)
                .balance(new BigDecimal("5000.00"))
                .status(PaymentStatus.PENDING)
                .dueDate(LocalDate.of(2024, 1, 10))
                .remarks("Monthly fee")
                .build();
    }

    @Test
    @DisplayName("Should create fee journal successfully")
    void shouldCreateFeeJournalSuccessfully() {
        // Given
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(feeJournalRepository.existsByStudentIdAndMonthAndYear(1L, "January", 2024))
                .thenReturn(false);
        when(feeJournalMapper.toEntity(any(FeeJournalRequestDTO.class))).thenReturn(feeJournal);
        when(feeJournalRepository.save(any(FeeJournal.class))).thenReturn(feeJournal);
        when(feeJournalMapper.toResponseDTO(any(FeeJournal.class))).thenReturn(responseDTO);

        // When
        FeeJournalResponseDTO result = feeJournalService.createFeeJournal(requestDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getMonth()).isEqualTo("January");
        assertThat(result.getYear()).isEqualTo(2024);
        assertThat(result.getAmountDue()).isEqualByComparingTo(new BigDecimal("5000.00"));

        verify(studentRepository).findById(1L);
        verify(feeJournalRepository).existsByStudentIdAndMonthAndYear(1L, "January", 2024);
        verify(feeJournalRepository).save(any(FeeJournal.class));
    }

    @Test
    @DisplayName("Should throw exception when student not found")
    void shouldThrowExceptionWhenStudentNotFound() {
        // Given
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        FeeJournalRequestDTO invalidDTO = FeeJournalRequestDTO.builder()
                .studentId(999L)
                .month("January")
                .year(2024)
                .amountDue(new BigDecimal("5000.00"))
                .amountPaid(BigDecimal.ZERO)
                .build();

        // When & Then
        assertThatThrownBy(() -> feeJournalService.createFeeJournal(invalidDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Student")
                .hasMessageContaining("999");

        verify(studentRepository).findById(999L);
        verify(feeJournalRepository, never()).save(any(FeeJournal.class));
    }

    @Test
    @DisplayName("Should throw exception when duplicate journal entry exists")
    void shouldThrowExceptionWhenDuplicateEntry() {
        // Given
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(feeJournalRepository.existsByStudentIdAndMonthAndYear(1L, "January", 2024))
                .thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> feeJournalService.createFeeJournal(requestDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("FeeJournal");

        verify(feeJournalRepository, never()).save(any(FeeJournal.class));
    }

    @Test
    @DisplayName("Should throw exception when amount paid exceeds amount due")
    void shouldThrowExceptionWhenAmountPaidExceedsAmountDue() {
        // Given
        FeeJournalRequestDTO invalidDTO = FeeJournalRequestDTO.builder()
                .studentId(1L)
                .month("January")
                .year(2024)
                .amountDue(new BigDecimal("5000.00"))
                .amountPaid(new BigDecimal("6000.00")) // Exceeds amount due
                .build();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(feeJournalRepository.existsByStudentIdAndMonthAndYear(1L, "January", 2024))
                .thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> feeJournalService.createFeeJournal(invalidDTO))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Amount paid cannot exceed amount due");

        verify(feeJournalRepository, never()).save(any(FeeJournal.class));
    }

    @Test
    @DisplayName("Should get fee journal by ID")
    void shouldGetFeeJournalById() {
        // Given
        when(feeJournalRepository.findById(1L)).thenReturn(Optional.of(feeJournal));
        when(feeJournalMapper.toResponseDTO(any(FeeJournal.class))).thenReturn(responseDTO);

        // When
        FeeJournalResponseDTO result = feeJournalService.getFeeJournal(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);

        verify(feeJournalRepository).findById(1L);
    }

    @Test
    @DisplayName("Should get all journals for student")
    void shouldGetAllJournalsForStudent() {
        // Given
        List<FeeJournal> journals = new ArrayList<>();
        journals.add(feeJournal);

        List<FeeJournalResponseDTO> responseDTOs = new ArrayList<>();
        responseDTOs.add(responseDTO);

        when(studentRepository.existsById(1L)).thenReturn(true);
        when(feeJournalRepository.findByStudentId(1L)).thenReturn(journals);
        when(feeJournalMapper.toResponseDTOList(anyList())).thenReturn(responseDTOs);

        // When
        List<FeeJournalResponseDTO> result = feeJournalService.getFeeJournalsByStudent(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);

        verify(feeJournalRepository).findByStudentId(1L);
    }

    @Test
    @DisplayName("Should get pending journals for student")
    void shouldGetPendingJournalsForStudent() {
        // Given
        List<FeeJournal> journals = new ArrayList<>();
        journals.add(feeJournal);

        List<FeeJournalResponseDTO> responseDTOs = new ArrayList<>();
        responseDTOs.add(responseDTO);

        when(studentRepository.existsById(1L)).thenReturn(true);
        when(feeJournalRepository.findPendingEntriesForStudent(1L)).thenReturn(journals);
        when(feeJournalMapper.toResponseDTOList(anyList())).thenReturn(responseDTOs);

        // When
        List<FeeJournalResponseDTO> result = feeJournalService.getPendingEntriesForStudent(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);

        verify(feeJournalRepository).findPendingEntriesForStudent(1L);
    }

    @Test
    @DisplayName("Should get journals by month and year")
    void shouldGetJournalsByMonthAndYear() {
        // Given
        List<FeeJournal> journals = new ArrayList<>();
        journals.add(feeJournal);

        List<FeeJournalResponseDTO> responseDTOs = new ArrayList<>();
        responseDTOs.add(responseDTO);

        when(feeJournalRepository.findByMonthAndYear("January", 2024)).thenReturn(journals);
        when(feeJournalMapper.toResponseDTOList(anyList())).thenReturn(responseDTOs);

        // When
        List<FeeJournalResponseDTO> result = feeJournalService.getFeeJournalsByMonthYear("January", 2024);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);

        verify(feeJournalRepository).findByMonthAndYear("January", 2024);
    }

    @Test
    @DisplayName("Should get journals by status")
    void shouldGetJournalsByStatus() {
        // Given
        List<FeeJournal> journals = new ArrayList<>();
        journals.add(feeJournal);

        List<FeeJournalResponseDTO> responseDTOs = new ArrayList<>();
        responseDTOs.add(responseDTO);

        when(feeJournalRepository.findByStatus(PaymentStatus.PENDING)).thenReturn(journals);
        when(feeJournalMapper.toResponseDTOList(anyList())).thenReturn(responseDTOs);

        // When
        List<FeeJournalResponseDTO> result = feeJournalService.getFeeJournalsByStatus(PaymentStatus.PENDING);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);

        verify(feeJournalRepository).findByStatus(PaymentStatus.PENDING);
    }

    @Test
    @DisplayName("Should get overdue journals")
    void shouldGetOverdueJournals() {
        // Given
        feeJournal.setStatus(PaymentStatus.OVERDUE);

        List<FeeJournal> journals = new ArrayList<>();
        journals.add(feeJournal);

        List<FeeJournalResponseDTO> responseDTOs = new ArrayList<>();
        FeeJournalResponseDTO overdueResponse = FeeJournalResponseDTO.builder()
                .id(1L)
                .status(PaymentStatus.OVERDUE)
                .build();
        responseDTOs.add(overdueResponse);

        when(feeJournalRepository.findByStatusOrderByDueDateAsc(PaymentStatus.OVERDUE)).thenReturn(journals);
        when(feeJournalMapper.toResponseDTOList(anyList())).thenReturn(responseDTOs);

        // When
        List<FeeJournalResponseDTO> result = feeJournalService.getOverdueJournals();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);

        verify(feeJournalRepository).findByStatusOrderByDueDateAsc(PaymentStatus.OVERDUE);
    }

    @Test
    @DisplayName("Should record full payment successfully")
    void shouldRecordFullPaymentSuccessfully() {
        // Given
        BigDecimal paymentAmount = new BigDecimal("5000.00");

        when(feeJournalRepository.findById(1L)).thenReturn(Optional.of(feeJournal));
        when(feeJournalRepository.save(any(FeeJournal.class))).thenReturn(feeJournal);
        when(feeJournalMapper.toResponseDTO(any(FeeJournal.class))).thenReturn(responseDTO);

        // When
        FeeJournalResponseDTO result = feeJournalService.recordPayment(1L, paymentAmount);

        // Then
        assertThat(result).isNotNull();

        verify(feeJournalRepository).findById(1L);
        verify(feeJournalRepository).save(feeJournal);
        assertThat(feeJournal.getAmountPaid()).isEqualByComparingTo(new BigDecimal("5000.00"));
    }

    @Test
    @DisplayName("Should record partial payment successfully")
    void shouldRecordPartialPaymentSuccessfully() {
        // Given
        BigDecimal paymentAmount = new BigDecimal("2000.00");

        when(feeJournalRepository.findById(1L)).thenReturn(Optional.of(feeJournal));
        when(feeJournalRepository.save(any(FeeJournal.class))).thenReturn(feeJournal);
        when(feeJournalMapper.toResponseDTO(any(FeeJournal.class))).thenReturn(responseDTO);

        // When
        FeeJournalResponseDTO result = feeJournalService.recordPayment(1L, paymentAmount);

        // Then
        assertThat(result).isNotNull();

        verify(feeJournalRepository).findById(1L);
        verify(feeJournalRepository).save(feeJournal);
        assertThat(feeJournal.getAmountPaid()).isEqualByComparingTo(new BigDecimal("2000.00"));
    }

    @Test
    @DisplayName("Should throw exception when payment exceeds due amount")
    void shouldThrowExceptionWhenPaymentExceedsDue() {
        // Given
        BigDecimal excessPayment = new BigDecimal("6000.00");

        when(feeJournalRepository.findById(1L)).thenReturn(Optional.of(feeJournal));

        // When & Then
        assertThatThrownBy(() -> feeJournalService.recordPayment(1L, excessPayment))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("exceeds remaining balance");

        verify(feeJournalRepository).findById(1L);
        verify(feeJournalRepository, never()).save(any(FeeJournal.class));
    }

    @Test
    @DisplayName("Should update fee journal successfully")
    void shouldUpdateFeeJournalSuccessfully() {
        // Given
        FeeJournalRequestDTO updateDTO = FeeJournalRequestDTO.builder()
                .studentId(1L)
                .month("January")
                .year(2024)
                .amountDue(new BigDecimal("5500.00")) // Updated amount
                .amountPaid(new BigDecimal("1000.00"))
                .build();

        when(feeJournalRepository.findById(1L)).thenReturn(Optional.of(feeJournal));
        when(feeJournalRepository.save(any(FeeJournal.class))).thenReturn(feeJournal);
        when(feeJournalMapper.toResponseDTO(any(FeeJournal.class))).thenReturn(responseDTO);
        doNothing().when(feeJournalMapper).updateEntityFromDTO(any(FeeJournalRequestDTO.class), any(FeeJournal.class));

        // When
        FeeJournalResponseDTO result = feeJournalService.updateFeeJournal(1L, updateDTO);

        // Then
        assertThat(result).isNotNull();

        verify(feeJournalRepository).findById(1L);
        verify(feeJournalMapper).updateEntityFromDTO(updateDTO, feeJournal);
        verify(feeJournalRepository).save(feeJournal);
    }

    @Test
    @DisplayName("Should delete fee journal successfully")
    void shouldDeleteFeeJournalSuccessfully() {
        // Given
        when(feeJournalRepository.findById(1L)).thenReturn(Optional.of(feeJournal));

        // When
        feeJournalService.deleteFeeJournal(1L);

        // Then
        verify(feeJournalRepository).findById(1L);
        verify(feeJournalRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting paid fee journal")
    void shouldThrowExceptionWhenDeletingPaidJournal() {
        // Given
        feeJournal.setStatus(PaymentStatus.PAID);

        when(feeJournalRepository.findById(1L)).thenReturn(Optional.of(feeJournal));

        // When & Then
        assertThatThrownBy(() -> feeJournalService.deleteFeeJournal(1L))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Cannot delete paid fee journal");

        verify(feeJournalRepository).findById(1L);
        verify(feeJournalRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should get total pending dues for student")
    void shouldGetTotalPendingDues() {
        // Given
        BigDecimal totalDues = new BigDecimal("15000.00");

        when(studentRepository.existsById(1L)).thenReturn(true);
        when(feeJournalRepository.getTotalPendingDues(1L)).thenReturn(totalDues);

        // When
        BigDecimal result = feeJournalService.getTotalPendingDues(1L);

        // Then
        assertThat(result).isEqualByComparingTo(totalDues);

        verify(feeJournalRepository).getTotalPendingDues(1L);
    }

    @Test
    @DisplayName("Should throw exception when payment amount is zero or negative")
    void shouldThrowExceptionWhenInvalidPaymentAmount() {
        // Given
        when(feeJournalRepository.findById(1L)).thenReturn(Optional.of(feeJournal));

        // When & Then
        assertThatThrownBy(() -> feeJournalService.recordPayment(1L, BigDecimal.ZERO))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Payment amount must be greater than 0");

        verify(feeJournalRepository, never()).save(any(FeeJournal.class));
    }
}
