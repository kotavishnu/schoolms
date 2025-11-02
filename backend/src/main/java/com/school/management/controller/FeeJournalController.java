package com.school.management.controller;

import com.school.management.dto.request.FeeJournalRequestDTO;
import com.school.management.dto.response.ApiResponse;
import com.school.management.dto.response.FeeJournalResponseDTO;
import com.school.management.model.PaymentStatus;
import com.school.management.service.FeeJournalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * REST controller for Fee Journal management operations.
 *
 * Provides endpoints for CRUD operations, payment tracking, and dues calculation.
 *
 * @author School Management Team
 */
@RestController
@RequestMapping("/api/fee-journals")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class FeeJournalController {

    private final FeeJournalService feeJournalService;

    /**
     * Create a new fee journal entry
     *
     * POST /api/fee-journals
     *
     * @param requestDTO Fee journal data
     * @return Created fee journal with HTTP 201
     */
    @PostMapping
    public ResponseEntity<ApiResponse<FeeJournalResponseDTO>> createFeeJournal(
            @Valid @RequestBody FeeJournalRequestDTO requestDTO) {

        log.info("POST /api/fee-journals - Creating fee journal for student ID: {}",
                requestDTO.getStudentId());

        FeeJournalResponseDTO feeJournal = feeJournalService.createFeeJournal(requestDTO);

        ApiResponse<FeeJournalResponseDTO> response = ApiResponse.success(
                "Fee journal created successfully", feeJournal);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get fee journal by ID
     *
     * GET /api/fee-journals/{id}
     *
     * @param id Fee journal ID
     * @return Fee journal details
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FeeJournalResponseDTO>> getFeeJournal(@PathVariable Long id) {
        log.info("GET /api/fee-journals/{}", id);

        FeeJournalResponseDTO feeJournal = feeJournalService.getFeeJournal(id);

        ApiResponse<FeeJournalResponseDTO> response = ApiResponse.success(feeJournal);

        return ResponseEntity.ok(response);
    }

    /**
     * Get all fee journals
     *
     * GET /api/fee-journals
     *
     * @return List of fee journals
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<FeeJournalResponseDTO>>> getAllFeeJournals() {
        log.info("GET /api/fee-journals");

        List<FeeJournalResponseDTO> feeJournals = feeJournalService.getAllFeeJournals();

        ApiResponse<List<FeeJournalResponseDTO>> response = ApiResponse.success(feeJournals);

        return ResponseEntity.ok(response);
    }

    /**
     * Get fee journals by student ID
     *
     * GET /api/fee-journals/student/{studentId}
     *
     * @param studentId Student ID
     * @return List of fee journals for the student
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<FeeJournalResponseDTO>>> getFeeJournalsByStudent(
            @PathVariable Long studentId) {

        log.info("GET /api/fee-journals/student/{}", studentId);

        List<FeeJournalResponseDTO> feeJournals = feeJournalService.getFeeJournalsByStudent(studentId);

        ApiResponse<List<FeeJournalResponseDTO>> response = ApiResponse.success(feeJournals);

        return ResponseEntity.ok(response);
    }

    /**
     * Get pending fee entries for student
     *
     * GET /api/fee-journals/student/{studentId}/pending
     *
     * @param studentId Student ID
     * @return List of pending fee journals
     */
    @GetMapping("/student/{studentId}/pending")
    public ResponseEntity<ApiResponse<List<FeeJournalResponseDTO>>> getPendingEntriesForStudent(
            @PathVariable Long studentId) {

        log.info("GET /api/fee-journals/student/{}/pending", studentId);

        List<FeeJournalResponseDTO> feeJournals = feeJournalService.getPendingEntriesForStudent(studentId);

        ApiResponse<List<FeeJournalResponseDTO>> response = ApiResponse.success(feeJournals);

        return ResponseEntity.ok(response);
    }

    /**
     * Get fee journals by month and year
     *
     * GET /api/fee-journals/by-month?month=January&year=2025
     *
     * @param month Month name
     * @param year Year
     * @return List of fee journals for the period
     */
    @GetMapping("/by-month")
    public ResponseEntity<ApiResponse<List<FeeJournalResponseDTO>>> getFeeJournalsByMonthYear(
            @RequestParam String month,
            @RequestParam Integer year) {

        log.info("GET /api/fee-journals/by-month?month={}&year={}", month, year);

        List<FeeJournalResponseDTO> feeJournals = feeJournalService.getFeeJournalsByMonthYear(month, year);

        ApiResponse<List<FeeJournalResponseDTO>> response = ApiResponse.success(feeJournals);

        return ResponseEntity.ok(response);
    }

    /**
     * Get fee journals by payment status
     *
     * GET /api/fee-journals/by-status/{status}
     *
     * @param status Payment status (PENDING, PAID, PARTIAL, OVERDUE)
     * @return List of fee journals with the status
     */
    @GetMapping("/by-status/{status}")
    public ResponseEntity<ApiResponse<List<FeeJournalResponseDTO>>> getFeeJournalsByStatus(
            @PathVariable PaymentStatus status) {

        log.info("GET /api/fee-journals/by-status/{}", status);

        List<FeeJournalResponseDTO> feeJournals = feeJournalService.getFeeJournalsByStatus(status);

        ApiResponse<List<FeeJournalResponseDTO>> response = ApiResponse.success(feeJournals);

        return ResponseEntity.ok(response);
    }

    /**
     * Get overdue fee journals
     *
     * GET /api/fee-journals/overdue
     *
     * @return List of overdue fee journals
     */
    @GetMapping("/overdue")
    public ResponseEntity<ApiResponse<List<FeeJournalResponseDTO>>> getOverdueJournals() {
        log.info("GET /api/fee-journals/overdue");

        List<FeeJournalResponseDTO> feeJournals = feeJournalService.getOverdueJournals();

        ApiResponse<List<FeeJournalResponseDTO>> response = ApiResponse.success(feeJournals);

        return ResponseEntity.ok(response);
    }

    /**
     * Get student dues summary
     *
     * GET /api/fee-journals/student/{studentId}/summary
     *
     * @param studentId Student ID
     * @return Summary with total pending and paid amounts
     */
    @GetMapping("/student/{studentId}/summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStudentDuesSummary(
            @PathVariable Long studentId) {

        log.info("GET /api/fee-journals/student/{}/summary", studentId);

        BigDecimal totalPending = feeJournalService.getTotalPendingDues(studentId);
        BigDecimal totalPaid = feeJournalService.getTotalPaidAmount(studentId);
        long pendingCount = feeJournalService.countPendingEntries(studentId);

        Map<String, Object> summary = Map.of(
                "studentId", studentId,
                "totalPendingDues", totalPending,
                "totalPaidAmount", totalPaid,
                "pendingEntriesCount", pendingCount
        );

        ApiResponse<Map<String, Object>> response = ApiResponse.success(summary);

        return ResponseEntity.ok(response);
    }

    /**
     * Update fee journal
     *
     * PUT /api/fee-journals/{id}
     *
     * @param id Fee journal ID
     * @param requestDTO Updated fee journal data
     * @return Updated fee journal
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FeeJournalResponseDTO>> updateFeeJournal(
            @PathVariable Long id,
            @Valid @RequestBody FeeJournalRequestDTO requestDTO) {

        log.info("PUT /api/fee-journals/{}", id);

        FeeJournalResponseDTO feeJournal = feeJournalService.updateFeeJournal(id, requestDTO);

        ApiResponse<FeeJournalResponseDTO> response = ApiResponse.success(
                "Fee journal updated successfully", feeJournal);

        return ResponseEntity.ok(response);
    }

    /**
     * Record payment for fee journal
     *
     * PATCH /api/fee-journals/{id}/payment
     *
     * @param id Fee journal ID
     * @param paymentData Payment amount in request body
     * @return Updated fee journal
     */
    @PatchMapping("/{id}/payment")
    public ResponseEntity<ApiResponse<FeeJournalResponseDTO>> recordPayment(
            @PathVariable Long id,
            @RequestBody Map<String, BigDecimal> paymentData) {

        log.info("PATCH /api/fee-journals/{}/payment", id);

        BigDecimal paymentAmount = paymentData.get("amount");
        FeeJournalResponseDTO feeJournal = feeJournalService.recordPayment(id, paymentAmount);

        ApiResponse<FeeJournalResponseDTO> response = ApiResponse.success(
                "Payment recorded successfully", feeJournal);

        return ResponseEntity.ok(response);
    }

    /**
     * Delete fee journal
     *
     * DELETE /api/fee-journals/{id}
     *
     * @param id Fee journal ID
     * @return Success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFeeJournal(@PathVariable Long id) {
        log.info("DELETE /api/fee-journals/{}", id);

        feeJournalService.deleteFeeJournal(id);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Fee journal deleted successfully")
                .build();

        return ResponseEntity.ok(response);
    }
}
