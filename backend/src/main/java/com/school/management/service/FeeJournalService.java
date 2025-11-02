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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service layer for Fee Journal management operations.
 *
 * Handles business logic for fee tracking, payment recording, and dues calculation.
 *
 * @author School Management Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FeeJournalService {

    private final FeeJournalRepository feeJournalRepository;
    private final StudentRepository studentRepository;
    private final FeeJournalMapper feeJournalMapper;

    /**
     * Create a new fee journal entry
     *
     * @param requestDTO Fee journal data
     * @return Created fee journal DTO
     * @throws ResourceNotFoundException if student not found
     * @throws DuplicateResourceException if journal entry already exists for student, month, year
     */
    @Transactional
    public FeeJournalResponseDTO createFeeJournal(FeeJournalRequestDTO requestDTO) {
        log.info("Creating fee journal for student ID: {}, Month: {}, Year: {}",
                requestDTO.getStudentId(), requestDTO.getMonth(), requestDTO.getYear());

        // Validate student exists
        Student student = studentRepository.findById(requestDTO.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", requestDTO.getStudentId()));

        // Check for duplicate entry
        if (feeJournalRepository.existsByStudentIdAndMonthAndYear(
                requestDTO.getStudentId(), requestDTO.getMonth(), requestDTO.getYear())) {
            throw new DuplicateResourceException(
                    "FeeJournal",
                    "studentId-month-year",
                    String.format("%d-%s-%d", requestDTO.getStudentId(), requestDTO.getMonth(), requestDTO.getYear()));
        }

        // Validate amount paid does not exceed amount due
        if (requestDTO.getAmountPaid().compareTo(requestDTO.getAmountDue()) > 0) {
            throw new ValidationException("Amount paid cannot exceed amount due");
        }

        // Map DTO to entity
        FeeJournal feeJournal = feeJournalMapper.toEntity(requestDTO);
        feeJournal.setStudent(student);

        // Save fee journal (balance and status calculated in @PrePersist)
        FeeJournal savedJournal = feeJournalRepository.save(feeJournal);

        log.info("Fee journal created successfully with ID: {}", savedJournal.getId());

        return feeJournalMapper.toResponseDTO(savedJournal);
    }

    /**
     * Get fee journal by ID
     *
     * @param id Fee journal ID
     * @return Fee journal DTO
     * @throws ResourceNotFoundException if fee journal not found
     */
    public FeeJournalResponseDTO getFeeJournal(Long id) {
        log.debug("Fetching fee journal with ID: {}", id);

        FeeJournal feeJournal = feeJournalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FeeJournal", "id", id));

        return feeJournalMapper.toResponseDTO(feeJournal);
    }

    /**
     * Get all fee journals
     *
     * @return List of fee journal DTOs
     */
    public List<FeeJournalResponseDTO> getAllFeeJournals() {
        log.debug("Fetching all fee journals");

        List<FeeJournal> feeJournals = feeJournalRepository.findAll();

        return feeJournalMapper.toResponseDTOList(feeJournals);
    }

    /**
     * Get fee journals by student ID
     *
     * @param studentId Student ID
     * @return List of fee journal DTOs
     */
    public List<FeeJournalResponseDTO> getFeeJournalsByStudent(Long studentId) {
        log.debug("Fetching fee journals for student ID: {}", studentId);

        // Validate student exists
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student", "id", studentId);
        }

        List<FeeJournal> feeJournals = feeJournalRepository.findByStudentId(studentId);

        return feeJournalMapper.toResponseDTOList(feeJournals);
    }

    /**
     * Get pending fee entries for student
     *
     * @param studentId Student ID
     * @return List of pending fee journal DTOs
     */
    public List<FeeJournalResponseDTO> getPendingEntriesForStudent(Long studentId) {
        log.debug("Fetching pending fee entries for student ID: {}", studentId);

        // Validate student exists
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student", "id", studentId);
        }

        List<FeeJournal> feeJournals = feeJournalRepository.findPendingEntriesForStudent(studentId);

        return feeJournalMapper.toResponseDTOList(feeJournals);
    }

    /**
     * Get fee journals by month and year
     *
     * @param month Month name
     * @param year Year
     * @return List of fee journal DTOs
     */
    public List<FeeJournalResponseDTO> getFeeJournalsByMonthYear(String month, Integer year) {
        log.debug("Fetching fee journals for month: {}, year: {}", month, year);

        List<FeeJournal> feeJournals = feeJournalRepository.findByMonthAndYear(month, year);

        return feeJournalMapper.toResponseDTOList(feeJournals);
    }

    /**
     * Get fee journals by payment status
     *
     * @param status Payment status
     * @return List of fee journal DTOs
     */
    public List<FeeJournalResponseDTO> getFeeJournalsByStatus(PaymentStatus status) {
        log.debug("Fetching fee journals by status: {}", status);

        List<FeeJournal> feeJournals = feeJournalRepository.findByStatus(status);

        return feeJournalMapper.toResponseDTOList(feeJournals);
    }

    /**
     * Get overdue fee journals
     *
     * @return List of overdue fee journal DTOs
     */
    public List<FeeJournalResponseDTO> getOverdueJournals() {
        log.debug("Fetching overdue fee journals");

        List<FeeJournal> feeJournals = feeJournalRepository.findByStatusOrderByDueDateAsc(PaymentStatus.OVERDUE);

        return feeJournalMapper.toResponseDTOList(feeJournals);
    }

    /**
     * Update existing fee journal
     *
     * @param id Fee journal ID
     * @param requestDTO Updated fee journal data
     * @return Updated fee journal DTO
     * @throws ResourceNotFoundException if fee journal not found
     * @throws ValidationException if amount paid exceeds amount due
     */
    @Transactional
    public FeeJournalResponseDTO updateFeeJournal(Long id, FeeJournalRequestDTO requestDTO) {
        log.info("Updating fee journal with ID: {}", id);

        // Fetch existing fee journal
        FeeJournal feeJournal = feeJournalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FeeJournal", "id", id));

        // Validate amount paid does not exceed amount due
        if (requestDTO.getAmountPaid().compareTo(requestDTO.getAmountDue()) > 0) {
            throw new ValidationException("Amount paid cannot exceed amount due");
        }

        // Update fields
        feeJournalMapper.updateEntityFromDTO(requestDTO, feeJournal);

        FeeJournal updatedJournal = feeJournalRepository.save(feeJournal);

        log.info("Fee journal updated successfully with ID: {}", updatedJournal.getId());

        return feeJournalMapper.toResponseDTO(updatedJournal);
    }

    /**
     * Record payment for fee journal
     *
     * @param id Fee journal ID
     * @param paymentAmount Payment amount
     * @return Updated fee journal DTO
     * @throws ResourceNotFoundException if fee journal not found
     * @throws ValidationException if payment amount is invalid
     */
    @Transactional
    public FeeJournalResponseDTO recordPayment(Long id, BigDecimal paymentAmount) {
        log.info("Recording payment for fee journal ID: {}, Amount: {}", id, paymentAmount);

        FeeJournal feeJournal = feeJournalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FeeJournal", "id", id));

        // Validate payment amount
        if (paymentAmount == null || paymentAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Payment amount must be greater than 0");
        }

        BigDecimal newAmountPaid = feeJournal.getAmountPaid().add(paymentAmount);

        if (newAmountPaid.compareTo(feeJournal.getAmountDue()) > 0) {
            throw new ValidationException(
                    String.format("Payment amount %s exceeds remaining balance %s",
                            paymentAmount, feeJournal.getBalance()));
        }

        feeJournal.setAmountPaid(newAmountPaid);

        FeeJournal updatedJournal = feeJournalRepository.save(feeJournal);

        log.info("Payment recorded successfully for fee journal ID: {}", id);

        return feeJournalMapper.toResponseDTO(updatedJournal);
    }

    /**
     * Get total pending dues for student
     *
     * @param studentId Student ID
     * @return Total pending amount
     */
    public BigDecimal getTotalPendingDues(Long studentId) {
        log.debug("Calculating total pending dues for student ID: {}", studentId);

        // Validate student exists
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student", "id", studentId);
        }

        BigDecimal total = feeJournalRepository.getTotalPendingDues(studentId);

        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * Get total paid amount for student
     *
     * @param studentId Student ID
     * @return Total paid amount
     */
    public BigDecimal getTotalPaidAmount(Long studentId) {
        log.debug("Calculating total paid amount for student ID: {}", studentId);

        // Validate student exists
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student", "id", studentId);
        }

        BigDecimal total = feeJournalRepository.getTotalPaidAmount(studentId);

        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * Count pending entries for student
     *
     * @param studentId Student ID
     * @return Count of pending entries
     */
    public long countPendingEntries(Long studentId) {
        log.debug("Counting pending entries for student ID: {}", studentId);

        // Validate student exists
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student", "id", studentId);
        }

        return feeJournalRepository.countPendingEntries(studentId);
    }

    /**
     * Delete fee journal
     *
     * @param id Fee journal ID
     * @throws ResourceNotFoundException if fee journal not found
     * @throws ValidationException if fee journal is already paid
     */
    @Transactional
    public void deleteFeeJournal(Long id) {
        log.info("Deleting fee journal with ID: {}", id);

        FeeJournal feeJournal = feeJournalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FeeJournal", "id", id));

        // Prevent deletion of paid entries
        if (PaymentStatus.PAID.equals(feeJournal.getStatus())) {
            throw new ValidationException("Cannot delete paid fee journal entry");
        }

        feeJournalRepository.deleteById(id);

        log.info("Fee journal deleted successfully with ID: {}", id);
    }
}
