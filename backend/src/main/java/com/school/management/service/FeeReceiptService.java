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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Map;

/**
 * Service layer for Fee Receipt management operations.
 *
 * Handles business logic for receipt generation, fee calculation, and payment recording.
 *
 * @author School Management Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FeeReceiptService {

    private final FeeReceiptRepository feeReceiptRepository;
    private final StudentRepository studentRepository;
    private final FeeJournalRepository feeJournalRepository;
    private final FeeReceiptMapper feeReceiptMapper;

    /**
     * Generate a new fee receipt
     *
     * @param requestDTO Fee receipt data
     * @return Created fee receipt DTO
     * @throws ResourceNotFoundException if student not found
     * @throws ValidationException if validation fails
     */
    @Transactional
    public FeeReceiptResponseDTO generateReceipt(FeeReceiptRequestDTO requestDTO) {
        log.info("Generating fee receipt for student ID: {}, Amount: {}",
                requestDTO.getStudentId(), requestDTO.getAmount());

        // Validate student exists
        Student student = studentRepository.findById(requestDTO.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", requestDTO.getStudentId()));

        // Validate payment method specific fields
        validatePaymentMethodFields(requestDTO);

        // Generate unique receipt number
        String receiptNumber = generateReceiptNumber();

        // Map DTO to entity
        FeeReceipt feeReceipt = feeReceiptMapper.toEntity(requestDTO);
        feeReceipt.setStudent(student);
        feeReceipt.setReceiptNumber(receiptNumber);

        // Save fee receipt
        FeeReceipt savedReceipt = feeReceiptRepository.save(feeReceipt);

        // Update fee journals for the months paid
        updateFeeJournals(student, requestDTO.getMonthsPaid(), savedReceipt, requestDTO.getAmount());

        log.info("Fee receipt generated successfully: {}", receiptNumber);

        return feeReceiptMapper.toResponseDTO(savedReceipt);
    }

    /**
     * Get fee receipt by ID
     *
     * @param id Receipt ID
     * @return Fee receipt DTO
     * @throws ResourceNotFoundException if receipt not found
     */
    public FeeReceiptResponseDTO getReceipt(Long id) {
        log.debug("Fetching fee receipt with ID: {}", id);

        FeeReceipt feeReceipt = feeReceiptRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FeeReceipt", "id", id));

        return feeReceiptMapper.toResponseDTO(feeReceipt);
    }

    /**
     * Get fee receipt by receipt number
     *
     * @param receiptNumber Unique receipt number
     * @return Fee receipt DTO
     * @throws ResourceNotFoundException if receipt not found
     */
    public FeeReceiptResponseDTO getReceiptByNumber(String receiptNumber) {
        log.debug("Fetching fee receipt with number: {}", receiptNumber);

        FeeReceipt feeReceipt = feeReceiptRepository.findByReceiptNumber(receiptNumber)
                .orElseThrow(() -> new ResourceNotFoundException("FeeReceipt", "receiptNumber", receiptNumber));

        return feeReceiptMapper.toResponseDTO(feeReceipt);
    }

    /**
     * Get all fee receipts
     *
     * @return List of fee receipt DTOs
     */
    public List<FeeReceiptResponseDTO> getAllReceipts() {
        log.debug("Fetching all fee receipts");

        List<FeeReceipt> receipts = feeReceiptRepository.findAll();

        return feeReceiptMapper.toResponseDTOList(receipts);
    }

    /**
     * Get fee receipts by student ID
     *
     * @param studentId Student ID
     * @return List of fee receipt DTOs
     */
    public List<FeeReceiptResponseDTO> getReceiptsByStudent(Long studentId) {
        log.debug("Fetching fee receipts for student ID: {}", studentId);

        // Validate student exists
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student", "id", studentId);
        }

        List<FeeReceipt> receipts = feeReceiptRepository.findByStudentIdOrderByPaymentDateDesc(studentId);

        return feeReceiptMapper.toResponseDTOList(receipts);
    }

    /**
     * Get fee receipts by date range
     *
     * @param startDate Start date
     * @param endDate End date
     * @return List of fee receipt DTOs
     */
    public List<FeeReceiptResponseDTO> getReceiptsByDateRange(LocalDate startDate, LocalDate endDate) {
        log.debug("Fetching fee receipts from {} to {}", startDate, endDate);

        List<FeeReceipt> receipts = feeReceiptRepository.findByPaymentDateBetween(startDate, endDate);

        return feeReceiptMapper.toResponseDTOList(receipts);
    }

    /**
     * Get fee receipts by payment method
     *
     * @param paymentMethod Payment method
     * @return List of fee receipt DTOs
     */
    public List<FeeReceiptResponseDTO> getReceiptsByPaymentMethod(PaymentMethod paymentMethod) {
        log.debug("Fetching fee receipts by payment method: {}", paymentMethod);

        List<FeeReceipt> receipts = feeReceiptRepository.findByPaymentMethod(paymentMethod);

        return feeReceiptMapper.toResponseDTOList(receipts);
    }

    /**
     * Get fee receipts for today
     *
     * @return List of fee receipt DTOs
     */
    public List<FeeReceiptResponseDTO> getTodayReceipts() {
        log.debug("Fetching today's fee receipts");

        List<FeeReceipt> receipts = feeReceiptRepository.findByPaymentDate(LocalDate.now());

        return feeReceiptMapper.toResponseDTOList(receipts);
    }

    /**
     * Get total collection for date range
     *
     * @param startDate Start date
     * @param endDate End date
     * @return Total amount collected
     */
    public BigDecimal getTotalCollection(LocalDate startDate, LocalDate endDate) {
        log.debug("Calculating total collection from {} to {}", startDate, endDate);

        BigDecimal total = feeReceiptRepository.getTotalCollection(startDate, endDate);

        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * Get total collection by payment method
     *
     * @param paymentMethod Payment method
     * @param startDate Start date
     * @param endDate End date
     * @return Total amount
     */
    public BigDecimal getTotalCollectionByMethod(PaymentMethod paymentMethod, LocalDate startDate, LocalDate endDate) {
        log.debug("Calculating total collection by method: {} from {} to {}",
                paymentMethod, startDate, endDate);

        BigDecimal total = feeReceiptRepository.getTotalCollectionByMethod(paymentMethod, startDate, endDate);

        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * Get collection summary for date range
     *
     * @param startDate Start date
     * @param endDate End date
     * @return Map with collection breakdown by payment method
     */
    public Map<String, Object> getCollectionSummary(LocalDate startDate, LocalDate endDate) {
        log.debug("Generating collection summary from {} to {}", startDate, endDate);

        BigDecimal totalCash = getTotalCollectionByMethod(PaymentMethod.CASH, startDate, endDate);
        BigDecimal totalOnline = getTotalCollectionByMethod(PaymentMethod.ONLINE, startDate, endDate);
        BigDecimal totalCard = getTotalCollectionByMethod(PaymentMethod.CARD, startDate, endDate);
        BigDecimal totalCheque = getTotalCollectionByMethod(PaymentMethod.CHEQUE, startDate, endDate);
        BigDecimal grandTotal = getTotalCollection(startDate, endDate);

        return Map.of(
                "startDate", startDate,
                "endDate", endDate,
                "cashCollection", totalCash,
                "onlineCollection", totalOnline,
                "cardCollection", totalCard,
                "chequeCollection", totalCheque,
                "grandTotal", grandTotal,
                "receiptCount", feeReceiptRepository.findByPaymentDateBetween(startDate, endDate).size()
        );
    }

    /**
     * Count receipts for student
     *
     * @param studentId Student ID
     * @return Number of receipts
     */
    public long countReceiptsForStudent(Long studentId) {
        log.debug("Counting receipts for student ID: {}", studentId);

        // Validate student exists
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student", "id", studentId);
        }

        return feeReceiptRepository.countByStudentId(studentId);
    }

    /**
     * Generate unique receipt number
     * Format: REC-YYYY-NNNNN
     *
     * @return Generated receipt number
     */
    private String generateReceiptNumber() {
        String year = String.valueOf(Year.now().getValue());
        List<FeeReceipt> lastReceipts = feeReceiptRepository.findLastReceiptForYear(year);

        int nextNumber = 1;
        if (!lastReceipts.isEmpty()) {
            String lastReceiptNumber = lastReceipts.get(0).getReceiptNumber();
            String[] parts = lastReceiptNumber.split("-");
            if (parts.length == 3) {
                nextNumber = Integer.parseInt(parts[2]) + 1;
            }
        }

        return String.format("REC-%s-%05d", year, nextNumber);
    }

    /**
     * Validate payment method specific fields
     *
     * @param requestDTO Fee receipt request DTO
     * @throws ValidationException if validation fails
     */
    private void validatePaymentMethodFields(FeeReceiptRequestDTO requestDTO) {
        PaymentMethod method = requestDTO.getPaymentMethod();

        switch (method) {
            case ONLINE, CARD -> {
                if (requestDTO.getTransactionId() == null || requestDTO.getTransactionId().isBlank()) {
                    throw new ValidationException(
                            "Transaction ID is required for " + method + " payments");
                }
            }
            case CHEQUE -> {
                if (requestDTO.getChequeNumber() == null || requestDTO.getChequeNumber().isBlank()) {
                    throw new ValidationException("Cheque number is required for CHEQUE payments");
                }
                if (requestDTO.getBankName() == null || requestDTO.getBankName().isBlank()) {
                    throw new ValidationException("Bank name is required for CHEQUE payments");
                }
            }
        }
    }

    /**
     * Update fee journals for months paid
     *
     * @param student Student entity
     * @param monthsPaid List of month names
     * @param receipt Fee receipt entity
     * @param totalAmount Total amount paid
     */
    @Transactional
    protected void updateFeeJournals(Student student, List<String> monthsPaid,
                                     FeeReceipt receipt, BigDecimal totalAmount) {
        log.debug("Updating fee journals for student ID: {}, Months: {}",
                student.getId(), monthsPaid);

        int year = LocalDate.now().getYear();
        BigDecimal amountPerMonth = totalAmount.divide(
                BigDecimal.valueOf(monthsPaid.size()), 2, java.math.RoundingMode.HALF_UP);

        for (String month : monthsPaid) {
            // Find or create fee journal entry
            FeeJournal journal = feeJournalRepository.findByStudentIdAndMonthAndYear(
                    student.getId(), month.trim(), year)
                    .orElseGet(() -> createFeeJournal(student, month.trim(), year, amountPerMonth));

            // Update payment details
            BigDecimal newAmountPaid = journal.getAmountPaid().add(amountPerMonth);
            journal.setAmountPaid(newAmountPaid);
            journal.setPaymentDate(LocalDate.now());
            journal.setReceipt(receipt);

            feeJournalRepository.save(journal);

            log.debug("Updated fee journal for month: {}, Amount paid: {}", month, newAmountPaid);
        }
    }

    /**
     * Create a new fee journal entry
     *
     * @param student Student entity
     * @param month Month name
     * @param year Year
     * @param amountDue Amount due
     * @return Created fee journal
     */
    private FeeJournal createFeeJournal(Student student, String month, int year, BigDecimal amountDue) {
        FeeJournal journal = FeeJournal.builder()
                .student(student)
                .month(month)
                .year(year)
                .amountDue(amountDue)
                .amountPaid(BigDecimal.ZERO)
                .balance(amountDue)
                .status(PaymentStatus.PENDING)
                .dueDate(LocalDate.of(year, getMonthNumber(month), 10)) // 10th of each month
                .build();

        return feeJournalRepository.save(journal);
    }

    /**
     * Get month number from month name
     *
     * @param monthName Month name
     * @return Month number (1-12)
     */
    private int getMonthNumber(String monthName) {
        return switch (monthName.toUpperCase()) {
            case "JANUARY" -> 1;
            case "FEBRUARY" -> 2;
            case "MARCH" -> 3;
            case "APRIL" -> 4;
            case "MAY" -> 5;
            case "JUNE" -> 6;
            case "JULY" -> 7;
            case "AUGUST" -> 8;
            case "SEPTEMBER" -> 9;
            case "OCTOBER" -> 10;
            case "NOVEMBER" -> 11;
            case "DECEMBER" -> 12;
            default -> 1;
        };
    }
}
