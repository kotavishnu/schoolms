package com.school.management.service;

import com.school.management.dto.request.FeeReceiptRequestDTO;
import com.school.management.dto.response.FeeReceiptResponseDTO;
import com.school.management.exception.ResourceNotFoundException;
import com.school.management.exception.ValidationException;
import com.school.management.mapper.FeeReceiptMapper;
import com.school.management.model.*;
import com.school.management.repository.FeeJournalRepository;
import com.school.management.repository.FeeReceiptRepository;
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
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for FeeReceiptService
 *
 * Uses Mockito to mock dependencies and test business logic.
 *
 * @author School Management Team
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FeeReceiptService Tests")
class FeeReceiptServiceTest {

    @Mock
    private FeeReceiptRepository feeReceiptRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private FeeJournalRepository feeJournalRepository;

    @Mock
    private FeeReceiptMapper feeReceiptMapper;

    @InjectMocks
    private FeeReceiptService feeReceiptService;

    private FeeReceiptRequestDTO requestDTO;
    private FeeReceipt feeReceipt;
    private FeeReceiptResponseDTO responseDTO;
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
        Map<String, Object> feeBreakdown = new HashMap<>();
        feeBreakdown.put("TUITION", new BigDecimal("5000.00"));

        requestDTO = FeeReceiptRequestDTO.builder()
                .studentId(1L)
                .amount(new BigDecimal("5000.00"))
                .paymentDate(LocalDate.now())
                .paymentMethod(PaymentMethod.CASH)
                .monthsPaid(List.of("January"))
                .feeBreakdown(feeBreakdown)
                .remarks("Monthly fee payment")
                .build();

        feeReceipt = FeeReceipt.builder()
                .id(1L)
                .student(student)
                .receiptNumber("REC-2024-00001")
                .amount(new BigDecimal("5000.00"))
                .paymentDate(LocalDate.now())
                .paymentMethod(PaymentMethod.CASH)
                .monthsPaid("January")
                .feeBreakdown(feeBreakdown)
                .remarks("Monthly fee payment")
                .build();

        responseDTO = FeeReceiptResponseDTO.builder()
                .id(1L)
                .receiptNumber("REC-2024-00001")
                .studentId(1L)
                .studentName("Rajesh Kumar")
                .amount(new BigDecimal("5000.00"))
                .paymentDate(LocalDate.now())
                .paymentMethod(PaymentMethod.CASH)
                .monthsPaid("January")
                .feeBreakdown(feeBreakdown)
                .remarks("Monthly fee payment")
                .build();
    }

    @Test
    @DisplayName("Should generate receipt successfully with CASH payment")
    void shouldGenerateReceiptWithCashPayment() {
        // Given
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(feeReceiptRepository.findLastReceiptForYear(anyString())).thenReturn(new ArrayList<>());
        when(feeReceiptMapper.toEntity(any(FeeReceiptRequestDTO.class))).thenReturn(feeReceipt);
        when(feeReceiptRepository.save(any(FeeReceipt.class))).thenReturn(feeReceipt);
        when(feeReceiptMapper.toResponseDTO(any(FeeReceipt.class))).thenReturn(responseDTO);
        when(feeJournalRepository.findByStudentIdAndMonthAndYear(anyLong(), anyString(), anyInt()))
                .thenReturn(Optional.empty());
        when(feeJournalRepository.save(any(FeeJournal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        FeeReceiptResponseDTO result = feeReceiptService.generateReceipt(requestDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getPaymentMethod()).isEqualTo(PaymentMethod.CASH);

        verify(studentRepository).findById(1L);
        verify(feeReceiptRepository).save(any(FeeReceipt.class));
    }

    @Test
    @DisplayName("Should generate receipt successfully with ONLINE payment")
    void shouldGenerateReceiptWithOnlinePayment() {
        // Given
        FeeReceiptRequestDTO onlineDTO = FeeReceiptRequestDTO.builder()
                .studentId(1L)
                .amount(new BigDecimal("5000.00"))
                .paymentDate(LocalDate.now())
                .paymentMethod(PaymentMethod.ONLINE)
                .transactionId("TXN123456789") // Required for online
                .monthsPaid(List.of("January"))
                .feeBreakdown(new HashMap<>())
                .build();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(feeReceiptRepository.findLastReceiptForYear(anyString())).thenReturn(new ArrayList<>());
        when(feeReceiptMapper.toEntity(any(FeeReceiptRequestDTO.class))).thenReturn(feeReceipt);
        when(feeReceiptRepository.save(any(FeeReceipt.class))).thenReturn(feeReceipt);
        when(feeReceiptMapper.toResponseDTO(any(FeeReceipt.class))).thenReturn(responseDTO);
        when(feeJournalRepository.findByStudentIdAndMonthAndYear(anyLong(), anyString(), anyInt()))
                .thenReturn(Optional.empty());
        when(feeJournalRepository.save(any(FeeJournal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        FeeReceiptResponseDTO result = feeReceiptService.generateReceipt(onlineDTO);

        // Then
        assertThat(result).isNotNull();

        verify(feeReceiptRepository).save(any(FeeReceipt.class));
    }

    @Test
    @DisplayName("Should generate receipt successfully with CHEQUE payment")
    void shouldGenerateReceiptWithChequePayment() {
        // Given
        FeeReceiptRequestDTO chequeDTO = FeeReceiptRequestDTO.builder()
                .studentId(1L)
                .amount(new BigDecimal("5000.00"))
                .paymentDate(LocalDate.now())
                .paymentMethod(PaymentMethod.CHEQUE)
                .chequeNumber("CHQ123456") // Required for cheque
                .bankName("HDFC Bank") // Required for cheque
                .monthsPaid(List.of("January"))
                .feeBreakdown(new HashMap<>())
                .build();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(feeReceiptRepository.findLastReceiptForYear(anyString())).thenReturn(new ArrayList<>());
        when(feeReceiptMapper.toEntity(any(FeeReceiptRequestDTO.class))).thenReturn(feeReceipt);
        when(feeReceiptRepository.save(any(FeeReceipt.class))).thenReturn(feeReceipt);
        when(feeReceiptMapper.toResponseDTO(any(FeeReceipt.class))).thenReturn(responseDTO);
        when(feeJournalRepository.findByStudentIdAndMonthAndYear(anyLong(), anyString(), anyInt()))
                .thenReturn(Optional.empty());
        when(feeJournalRepository.save(any(FeeJournal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        FeeReceiptResponseDTO result = feeReceiptService.generateReceipt(chequeDTO);

        // Then
        assertThat(result).isNotNull();

        verify(feeReceiptRepository).save(any(FeeReceipt.class));
    }

    @Test
    @DisplayName("Should generate receipt successfully with CARD payment")
    void shouldGenerateReceiptWithCardPayment() {
        // Given
        FeeReceiptRequestDTO cardDTO = FeeReceiptRequestDTO.builder()
                .studentId(1L)
                .amount(new BigDecimal("5000.00"))
                .paymentDate(LocalDate.now())
                .paymentMethod(PaymentMethod.CARD)
                .transactionId("CARD123456") // Required for card
                .monthsPaid(List.of("January"))
                .feeBreakdown(new HashMap<>())
                .build();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(feeReceiptRepository.findLastReceiptForYear(anyString())).thenReturn(new ArrayList<>());
        when(feeReceiptMapper.toEntity(any(FeeReceiptRequestDTO.class))).thenReturn(feeReceipt);
        when(feeReceiptRepository.save(any(FeeReceipt.class))).thenReturn(feeReceipt);
        when(feeReceiptMapper.toResponseDTO(any(FeeReceipt.class))).thenReturn(responseDTO);
        when(feeJournalRepository.findByStudentIdAndMonthAndYear(anyLong(), anyString(), anyInt()))
                .thenReturn(Optional.empty());
        when(feeJournalRepository.save(any(FeeJournal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        FeeReceiptResponseDTO result = feeReceiptService.generateReceipt(cardDTO);

        // Then
        assertThat(result).isNotNull();

        verify(feeReceiptRepository).save(any(FeeReceipt.class));
    }

    @Test
    @DisplayName("Should auto-generate receipt number in format REC-YYYY-NNNNN")
    void shouldAutoGenerateReceiptNumber() {
        // Given - Test receipt number generation logic
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(feeReceiptRepository.findLastReceiptForYear(anyString())).thenReturn(new ArrayList<>());
        when(feeReceiptMapper.toEntity(any(FeeReceiptRequestDTO.class))).thenReturn(feeReceipt);
        when(feeReceiptRepository.save(any(FeeReceipt.class))).thenAnswer(invocation -> {
            FeeReceipt receipt = invocation.getArgument(0);
            assertThat(receipt.getReceiptNumber()).matches("REC-\\d{4}-\\d{5}");
            return receipt;
        });
        when(feeReceiptMapper.toResponseDTO(any(FeeReceipt.class))).thenReturn(responseDTO);
        when(feeJournalRepository.findByStudentIdAndMonthAndYear(anyLong(), anyString(), anyInt()))
                .thenReturn(Optional.empty());
        when(feeJournalRepository.save(any(FeeJournal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        feeReceiptService.generateReceipt(requestDTO);

        // Then
        verify(feeReceiptRepository).save(argThat(receipt ->
                receipt.getReceiptNumber() != null && receipt.getReceiptNumber().startsWith("REC-")
        ));
    }

    @Test
    @DisplayName("Should throw exception when student not found")
    void shouldThrowExceptionWhenStudentNotFound() {
        // Given
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        FeeReceiptRequestDTO invalidDTO = FeeReceiptRequestDTO.builder()
                .studentId(999L)
                .amount(new BigDecimal("5000.00"))
                .paymentDate(LocalDate.now())
                .paymentMethod(PaymentMethod.CASH)
                .monthsPaid(List.of("January"))
                .feeBreakdown(new HashMap<>())
                .build();

        // When & Then
        assertThatThrownBy(() -> feeReceiptService.generateReceipt(invalidDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Student")
                .hasMessageContaining("999");

        verify(feeReceiptRepository, never()).save(any(FeeReceipt.class));
    }

    @Test
    @DisplayName("Should validate transactionId required for ONLINE payment")
    void shouldValidateTransactionIdForOnlinePayment() {
        // Given
        FeeReceiptRequestDTO invalidDTO = FeeReceiptRequestDTO.builder()
                .studentId(1L)
                .amount(new BigDecimal("5000.00"))
                .paymentDate(LocalDate.now())
                .paymentMethod(PaymentMethod.ONLINE)
                .monthsPaid(List.of("January"))
                .feeBreakdown(new HashMap<>())
                // Missing transactionId
                .build();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        // When & Then
        assertThatThrownBy(() -> feeReceiptService.generateReceipt(invalidDTO))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Transaction ID is required");

        verify(feeReceiptRepository, never()).save(any(FeeReceipt.class));
    }

    @Test
    @DisplayName("Should validate cheque details required for CHEQUE payment")
    void shouldValidateChequeDetailsForChequePayment() {
        // Given - Missing cheque number
        FeeReceiptRequestDTO invalidDTO = FeeReceiptRequestDTO.builder()
                .studentId(1L)
                .amount(new BigDecimal("5000.00"))
                .paymentDate(LocalDate.now())
                .paymentMethod(PaymentMethod.CHEQUE)
                .bankName("HDFC Bank")
                .monthsPaid(List.of("January"))
                .feeBreakdown(new HashMap<>())
                // Missing chequeNumber
                .build();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        // When & Then
        assertThatThrownBy(() -> feeReceiptService.generateReceipt(invalidDTO))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Cheque number is required");

        verify(feeReceiptRepository, never()).save(any(FeeReceipt.class));
    }

    @Test
    @DisplayName("Should process multiple months paid")
    void shouldProcessMultipleMonthsPaid() {
        // Given
        FeeReceiptRequestDTO multiMonthDTO = FeeReceiptRequestDTO.builder()
                .studentId(1L)
                .amount(new BigDecimal("15000.00"))
                .paymentDate(LocalDate.now())
                .paymentMethod(PaymentMethod.CASH)
                .monthsPaid(List.of("January", "February", "March")) // 3 months
                .feeBreakdown(new HashMap<>())
                .build();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(feeReceiptRepository.findLastReceiptForYear(anyString())).thenReturn(new ArrayList<>());
        when(feeReceiptMapper.toEntity(any(FeeReceiptRequestDTO.class))).thenReturn(feeReceipt);
        when(feeReceiptRepository.save(any(FeeReceipt.class))).thenReturn(feeReceipt);
        when(feeReceiptMapper.toResponseDTO(any(FeeReceipt.class))).thenReturn(responseDTO);
        when(feeJournalRepository.findByStudentIdAndMonthAndYear(anyLong(), anyString(), anyInt()))
                .thenReturn(Optional.empty());
        when(feeJournalRepository.save(any(FeeJournal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        FeeReceiptResponseDTO result = feeReceiptService.generateReceipt(multiMonthDTO);

        // Then
        assertThat(result).isNotNull();

        // Verify fee journals updated for all 3 months (2 saves per month: create + update)
        verify(feeJournalRepository, times(6)).save(any(FeeJournal.class));
    }

    @Test
    @DisplayName("Should get receipt by ID")
    void shouldGetReceiptById() {
        // Given
        when(feeReceiptRepository.findById(1L)).thenReturn(Optional.of(feeReceipt));
        when(feeReceiptMapper.toResponseDTO(any(FeeReceipt.class))).thenReturn(responseDTO);

        // When
        FeeReceiptResponseDTO result = feeReceiptService.getReceipt(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);

        verify(feeReceiptRepository).findById(1L);
    }

    @Test
    @DisplayName("Should get receipt by receipt number")
    void shouldGetReceiptByReceiptNumber() {
        // Given
        when(feeReceiptRepository.findByReceiptNumber("REC-2024-00001")).thenReturn(Optional.of(feeReceipt));
        when(feeReceiptMapper.toResponseDTO(any(FeeReceipt.class))).thenReturn(responseDTO);

        // When
        FeeReceiptResponseDTO result = feeReceiptService.getReceiptByNumber("REC-2024-00001");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getReceiptNumber()).isEqualTo("REC-2024-00001");

        verify(feeReceiptRepository).findByReceiptNumber("REC-2024-00001");
    }

    @Test
    @DisplayName("Should get receipts for student")
    void shouldGetReceiptsForStudent() {
        // Given
        List<FeeReceipt> receipts = new ArrayList<>();
        receipts.add(feeReceipt);

        List<FeeReceiptResponseDTO> responseDTOs = new ArrayList<>();
        responseDTOs.add(responseDTO);

        when(studentRepository.existsById(1L)).thenReturn(true);
        when(feeReceiptRepository.findByStudentIdOrderByPaymentDateDesc(1L)).thenReturn(receipts);
        when(feeReceiptMapper.toResponseDTOList(anyList())).thenReturn(responseDTOs);

        // When
        List<FeeReceiptResponseDTO> result = feeReceiptService.getReceiptsByStudent(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);

        verify(feeReceiptRepository).findByStudentIdOrderByPaymentDateDesc(1L);
    }

    @Test
    @DisplayName("Should get receipts by date range")
    void shouldGetReceiptsByDateRange() {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        List<FeeReceipt> receipts = new ArrayList<>();
        receipts.add(feeReceipt);

        List<FeeReceiptResponseDTO> responseDTOs = new ArrayList<>();
        responseDTOs.add(responseDTO);

        when(feeReceiptRepository.findByPaymentDateBetween(startDate, endDate)).thenReturn(receipts);
        when(feeReceiptMapper.toResponseDTOList(anyList())).thenReturn(responseDTOs);

        // When
        List<FeeReceiptResponseDTO> result = feeReceiptService.getReceiptsByDateRange(startDate, endDate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);

        verify(feeReceiptRepository).findByPaymentDateBetween(startDate, endDate);
    }

    @Test
    @DisplayName("Should get receipts by payment method")
    void shouldGetReceiptsByPaymentMethod() {
        // Given
        List<FeeReceipt> receipts = new ArrayList<>();
        receipts.add(feeReceipt);

        List<FeeReceiptResponseDTO> responseDTOs = new ArrayList<>();
        responseDTOs.add(responseDTO);

        when(feeReceiptRepository.findByPaymentMethod(PaymentMethod.CASH)).thenReturn(receipts);
        when(feeReceiptMapper.toResponseDTOList(anyList())).thenReturn(responseDTOs);

        // When
        List<FeeReceiptResponseDTO> result = feeReceiptService.getReceiptsByPaymentMethod(PaymentMethod.CASH);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);

        verify(feeReceiptRepository).findByPaymentMethod(PaymentMethod.CASH);
    }

    @Test
    @DisplayName("Should get today's receipts")
    void shouldGetTodaysReceipts() {
        // Given
        List<FeeReceipt> receipts = new ArrayList<>();
        receipts.add(feeReceipt);

        List<FeeReceiptResponseDTO> responseDTOs = new ArrayList<>();
        responseDTOs.add(responseDTO);

        when(feeReceiptRepository.findByPaymentDate(any(LocalDate.class))).thenReturn(receipts);
        when(feeReceiptMapper.toResponseDTOList(anyList())).thenReturn(responseDTOs);

        // When
        List<FeeReceiptResponseDTO> result = feeReceiptService.getTodayReceipts();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);

        verify(feeReceiptRepository).findByPaymentDate(any(LocalDate.class));
    }

    @Test
    @DisplayName("Should calculate total collection for date range")
    void shouldCalculateTotalCollectionForDateRange() {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        BigDecimal totalCollection = new BigDecimal("50000.00");

        when(feeReceiptRepository.getTotalCollection(startDate, endDate)).thenReturn(totalCollection);

        // When
        BigDecimal result = feeReceiptService.getTotalCollection(startDate, endDate);

        // Then
        assertThat(result).isEqualByComparingTo(totalCollection);

        verify(feeReceiptRepository).getTotalCollection(startDate, endDate);
    }

    @Test
    @DisplayName("Should calculate collection by payment method")
    void shouldCalculateCollectionByPaymentMethod() {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        BigDecimal cashCollection = new BigDecimal("30000.00");

        when(feeReceiptRepository.getTotalCollectionByMethod(PaymentMethod.CASH, startDate, endDate))
                .thenReturn(cashCollection);

        // When
        BigDecimal result = feeReceiptService.getTotalCollectionByMethod(PaymentMethod.CASH, startDate, endDate);

        // Then
        assertThat(result).isEqualByComparingTo(cashCollection);

        verify(feeReceiptRepository).getTotalCollectionByMethod(PaymentMethod.CASH, startDate, endDate);
    }

    @Test
    @DisplayName("Should get collection summary")
    void shouldGetCollectionSummary() {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        when(feeReceiptRepository.getTotalCollectionByMethod(eq(PaymentMethod.CASH), any(), any()))
                .thenReturn(new BigDecimal("20000.00"));
        when(feeReceiptRepository.getTotalCollectionByMethod(eq(PaymentMethod.ONLINE), any(), any()))
                .thenReturn(new BigDecimal("15000.00"));
        when(feeReceiptRepository.getTotalCollectionByMethod(eq(PaymentMethod.CARD), any(), any()))
                .thenReturn(new BigDecimal("10000.00"));
        when(feeReceiptRepository.getTotalCollectionByMethod(eq(PaymentMethod.CHEQUE), any(), any()))
                .thenReturn(new BigDecimal("5000.00"));
        when(feeReceiptRepository.getTotalCollection(startDate, endDate))
                .thenReturn(new BigDecimal("50000.00"));
        when(feeReceiptRepository.findByPaymentDateBetween(startDate, endDate))
                .thenReturn(List.of(feeReceipt));

        // When
        Map<String, Object> summary = feeReceiptService.getCollectionSummary(startDate, endDate);

        // Then
        assertThat(summary).isNotNull();
        assertThat(summary).containsKeys("cashCollection", "onlineCollection", "cardCollection",
                "chequeCollection", "grandTotal", "receiptCount");
        assertThat(summary.get("grandTotal")).isEqualTo(new BigDecimal("50000.00"));

        verify(feeReceiptRepository).getTotalCollection(startDate, endDate);
    }

    @Test
    @DisplayName("Should count receipts for student")
    void shouldCountReceiptsForStudent() {
        // Given
        when(studentRepository.existsById(1L)).thenReturn(true);
        when(feeReceiptRepository.countByStudentId(1L)).thenReturn(5L);

        // When
        long count = feeReceiptService.countReceiptsForStudent(1L);

        // Then
        assertThat(count).isEqualTo(5L);

        verify(feeReceiptRepository).countByStudentId(1L);
    }
}
