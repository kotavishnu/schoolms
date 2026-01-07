package com.school.management.service;

import com.school.management.dto.FeeReceiptDTO;
import com.school.management.exception.ResourceNotFoundException;
import com.school.management.model.*;
import com.school.management.repository.FeeJournalRepository;
import com.school.management.repository.FeeReceiptRepository;
import com.school.management.repository.SchoolClassRepository;
import com.school.management.repository.StudentRepository;
import com.school.management.util.FeeCalculationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FeeReceiptService {

    private final FeeReceiptRepository feeReceiptRepository;
    private final StudentRepository studentRepository;
    private final SchoolClassRepository classRepository;
    private final FeeJournalRepository feeJournalRepository;
    private final FeeCalculationService feeCalculationService;

    public FeeReceiptDTO generateReceipt(FeeReceiptDTO dto) {
        // Validate student exists
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", dto.getStudentId()));

        SchoolClass schoolClass = classRepository.findById(student.getClassId())
                .orElse(null);

        // Check if this is first month payment
        Long previousPayments = feeReceiptRepository.countByStudentId(dto.getStudentId());
        Boolean isFirstMonth = previousPayments == 0;

        // Calculate fee using Drools
        FeeCalculationResult calculationResult = feeCalculationService.calculateFee(
            student.getId(),
            schoolClass != null ? schoolClass.getClassNumber() : null,
            dto.getMonthYear() != null ? extractAcademicYear(dto.getMonthYear()) : getCurrentAcademicYear(),
            isFirstMonth
        );

        // Generate receipt number
        String receiptNumber = generateReceiptNumber();

        // Create receipt
        FeeReceipt receipt = FeeReceipt.builder()
                .receiptNumber(receiptNumber)
                .studentId(dto.getStudentId())
                .amount(calculationResult.getTotalAmount())
                .paymentDate(dto.getPaymentDate() != null ? dto.getPaymentDate() : LocalDate.now())
                .paymentMethod(dto.getPaymentMethod())
                .monthYear(dto.getMonthYear())
                .remarks(buildRemarks(calculationResult))
                .generatedAt(LocalDateTime.now())
                .build();

        FeeReceipt savedReceipt = feeReceiptRepository.save(receipt);

        // Create journal entry
        createJournalEntry(savedReceipt, dto.getMonthYear());

        return convertToDTO(savedReceipt, student, schoolClass);
    }

    @Transactional(readOnly = true)
    public FeeReceiptDTO getReceiptById(Long id) {
        FeeReceipt receipt = feeReceiptRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fee Receipt", "id", id));

        Student student = studentRepository.findById(receipt.getStudentId()).orElse(null);
        SchoolClass schoolClass = student != null ?
            classRepository.findById(student.getClassId()).orElse(null) : null;

        return convertToDTO(receipt, student, schoolClass);
    }

    @Transactional(readOnly = true)
    public FeeReceiptDTO getReceiptByReceiptNumber(String receiptNumber) {
        FeeReceipt receipt = feeReceiptRepository.findByReceiptNumber(receiptNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Fee Receipt", "receiptNumber", receiptNumber));

        Student student = studentRepository.findById(receipt.getStudentId()).orElse(null);
        SchoolClass schoolClass = student != null ?
            classRepository.findById(student.getClassId()).orElse(null) : null;

        return convertToDTO(receipt, student, schoolClass);
    }

    @Transactional(readOnly = true)
    public List<FeeReceiptDTO> getReceiptsByStudentId(Long studentId) {
        List<FeeReceipt> receipts = feeReceiptRepository.findByStudentIdOrderByPaymentDateDesc(studentId);
        Student student = studentRepository.findById(studentId).orElse(null);
        SchoolClass schoolClass = student != null ?
            classRepository.findById(student.getClassId()).orElse(null) : null;

        return receipts.stream()
                .map(receipt -> convertToDTO(receipt, student, schoolClass))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FeeReceiptDTO> getAllReceipts() {
        return feeReceiptRepository.findAll().stream()
                .map(receipt -> {
                    Student student = studentRepository.findById(receipt.getStudentId()).orElse(null);
                    SchoolClass schoolClass = student != null ?
                        classRepository.findById(student.getClassId()).orElse(null) : null;
                    return convertToDTO(receipt, student, schoolClass);
                })
                .collect(Collectors.toList());
    }

    private void createJournalEntry(FeeReceipt receipt, String monthYear) {
        FeeJournal journal = FeeJournal.builder()
                .studentId(receipt.getStudentId())
                .feeReceiptId(receipt.getId())
                .paymentDate(receipt.getPaymentDate())
                .amount(receipt.getAmount())
                .monthPaid(monthYear)
                .isPending(false)
                .academicYear(extractAcademicYear(monthYear))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        feeJournalRepository.save(journal);
    }

    private String generateReceiptNumber() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = feeReceiptRepository.count() + 1;
        return "RCP-" + dateStr + "-" + String.format("%05d", count);
    }

    private String buildRemarks(FeeCalculationResult result) {
        StringBuilder remarks = new StringBuilder("Fee Components: ");
        result.getFeeComponents().forEach((component, amount) ->
            remarks.append(component).append(": ₹").append(amount).append(", ")
        );
        return remarks.toString();
    }

    private String extractAcademicYear(String monthYear) {
        // Example: "January 2024" -> "2024-2025"
        if (monthYear == null) return getCurrentAcademicYear();
        String[] parts = monthYear.split(" ");
        if (parts.length == 2) {
            int year = Integer.parseInt(parts[1]);
            return year + "-" + (year + 1);
        }
        return getCurrentAcademicYear();
    }

    private String getCurrentAcademicYear() {
        int currentYear = LocalDate.now().getYear();
        return currentYear + "-" + (currentYear + 1);
    }

    private FeeReceiptDTO convertToDTO(FeeReceipt receipt, Student student, SchoolClass schoolClass) {
        FeeReceiptDTO dto = FeeReceiptDTO.builder()
                .id(receipt.getId())
                .receiptNumber(receipt.getReceiptNumber())
                .studentId(receipt.getStudentId())
                .amount(receipt.getAmount())
                .paymentDate(receipt.getPaymentDate())
                .paymentMethod(receipt.getPaymentMethod())
                .monthYear(receipt.getMonthYear())
                .remarks(receipt.getRemarks())
                .build();

        if (student != null) {
            dto.setStudentName(student.getFullName());
        }

        if (schoolClass != null) {
            dto.setClassName("Class " + schoolClass.getClassNumber() +
                    (schoolClass.getSection() != null ? " " + schoolClass.getSection() : ""));
        }

        return dto;
    }
}
