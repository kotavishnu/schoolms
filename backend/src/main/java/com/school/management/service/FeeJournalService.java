package com.school.management.service;

import com.school.management.dto.FeeJournalDTO;
import com.school.management.exception.ResourceNotFoundException;
import com.school.management.model.FeeJournal;
import com.school.management.model.FeeReceipt;
import com.school.management.model.Student;
import com.school.management.repository.FeeJournalRepository;
import com.school.management.repository.FeeReceiptRepository;
import com.school.management.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FeeJournalService {

    private final FeeJournalRepository feeJournalRepository;
    private final StudentRepository studentRepository;
    private final FeeReceiptRepository feeReceiptRepository;

    @Transactional(readOnly = true)
    public FeeJournalDTO getJournalById(Long id) {
        FeeJournal journal = feeJournalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fee Journal", "id", id));
        return convertToDTO(journal);
    }

    @Transactional(readOnly = true)
    public List<FeeJournalDTO> getAllJournals() {
        return feeJournalRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FeeJournalDTO> getJournalsByStudentId(Long studentId) {
        return feeJournalRepository.findByStudentId(studentId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FeeJournalDTO> getPendingFeesByStudentId(Long studentId) {
        return feeJournalRepository.findByStudentIdAndIsPending(studentId, true).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FeeJournalDTO> getPendingFeesByAcademicYear(String academicYear) {
        return feeJournalRepository.findByAcademicYearAndIsPending(academicYear, true).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Long getPendingFeesCount(Long studentId) {
        return feeJournalRepository.countByStudentIdAndIsPending(studentId, true);
    }

    private FeeJournalDTO convertToDTO(FeeJournal journal) {
        FeeJournalDTO dto = FeeJournalDTO.builder()
                .id(journal.getId())
                .studentId(journal.getStudentId())
                .feeReceiptId(journal.getFeeReceiptId())
                .paymentDate(journal.getPaymentDate())
                .amount(journal.getAmount())
                .monthPaid(journal.getMonthPaid())
                .isPending(journal.getIsPending())
                .academicYear(journal.getAcademicYear())
                .build();

        // Get student name
        studentRepository.findById(journal.getStudentId())
                .ifPresent(student -> dto.setStudentName(student.getFullName()));

        // Get receipt number
        if (journal.getFeeReceiptId() != null) {
            feeReceiptRepository.findById(journal.getFeeReceiptId())
                    .ifPresent(receipt -> dto.setReceiptNumber(receipt.getReceiptNumber()));
        }

        return dto;
    }
}
