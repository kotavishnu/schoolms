package com.school.management.controller;

import com.school.management.dto.request.FeeReceiptRequestDTO;
import com.school.management.dto.response.ApiResponse;
import com.school.management.dto.response.FeeReceiptResponseDTO;
import com.school.management.model.PaymentMethod;
import com.school.management.service.FeeReceiptService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * REST controller for Fee Receipt management operations.
 *
 * Provides endpoints for receipt generation, retrieval, and collection reports.
 *
 * @author School Management Team
 */
@RestController
@RequestMapping("/api/fee-receipts")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class FeeReceiptController {

    private final FeeReceiptService feeReceiptService;

    /**
     * Generate a new fee receipt
     *
     * POST /api/fee-receipts
     *
     * @param requestDTO Fee receipt data
     * @return Created fee receipt with HTTP 201
     */
    @PostMapping
    public ResponseEntity<ApiResponse<FeeReceiptResponseDTO>> generateReceipt(
            @Valid @RequestBody FeeReceiptRequestDTO requestDTO) {

        log.info("POST /api/fee-receipts - Generating receipt for student ID: {}",
                requestDTO.getStudentId());

        FeeReceiptResponseDTO receipt = feeReceiptService.generateReceipt(requestDTO);

        ApiResponse<FeeReceiptResponseDTO> response = ApiResponse.success(
                "Fee receipt generated successfully", receipt);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get fee receipt by ID
     *
     * GET /api/fee-receipts/{id}
     *
     * @param id Receipt ID
     * @return Fee receipt details
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FeeReceiptResponseDTO>> getReceipt(@PathVariable Long id) {
        log.info("GET /api/fee-receipts/{}", id);

        FeeReceiptResponseDTO receipt = feeReceiptService.getReceipt(id);

        ApiResponse<FeeReceiptResponseDTO> response = ApiResponse.success(receipt);

        return ResponseEntity.ok(response);
    }

    /**
     * Get fee receipt by receipt number
     *
     * GET /api/fee-receipts/number/{receiptNumber}
     *
     * @param receiptNumber Unique receipt number
     * @return Fee receipt details
     */
    @GetMapping("/number/{receiptNumber}")
    public ResponseEntity<ApiResponse<FeeReceiptResponseDTO>> getReceiptByNumber(
            @PathVariable String receiptNumber) {

        log.info("GET /api/fee-receipts/number/{}", receiptNumber);

        FeeReceiptResponseDTO receipt = feeReceiptService.getReceiptByNumber(receiptNumber);

        ApiResponse<FeeReceiptResponseDTO> response = ApiResponse.success(receipt);

        return ResponseEntity.ok(response);
    }

    /**
     * Get all fee receipts
     *
     * GET /api/fee-receipts
     *
     * @return List of fee receipts
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<FeeReceiptResponseDTO>>> getAllReceipts() {
        log.info("GET /api/fee-receipts");

        List<FeeReceiptResponseDTO> receipts = feeReceiptService.getAllReceipts();

        ApiResponse<List<FeeReceiptResponseDTO>> response = ApiResponse.success(receipts);

        return ResponseEntity.ok(response);
    }

    /**
     * Get fee receipts by student ID
     *
     * GET /api/fee-receipts/student/{studentId}
     *
     * @param studentId Student ID
     * @return List of fee receipts for the student
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<FeeReceiptResponseDTO>>> getReceiptsByStudent(
            @PathVariable Long studentId) {

        log.info("GET /api/fee-receipts/student/{}", studentId);

        List<FeeReceiptResponseDTO> receipts = feeReceiptService.getReceiptsByStudent(studentId);

        ApiResponse<List<FeeReceiptResponseDTO>> response = ApiResponse.success(receipts);

        return ResponseEntity.ok(response);
    }

    /**
     * Get fee receipts by date range
     *
     * GET /api/fee-receipts/by-date?startDate=2025-01-01&endDate=2025-01-31
     *
     * @param startDate Start date
     * @param endDate End date
     * @return List of fee receipts in the date range
     */
    @GetMapping("/by-date")
    public ResponseEntity<ApiResponse<List<FeeReceiptResponseDTO>>> getReceiptsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("GET /api/fee-receipts/by-date?startDate={}&endDate={}", startDate, endDate);

        List<FeeReceiptResponseDTO> receipts = feeReceiptService.getReceiptsByDateRange(startDate, endDate);

        ApiResponse<List<FeeReceiptResponseDTO>> response = ApiResponse.success(receipts);

        return ResponseEntity.ok(response);
    }

    /**
     * Get fee receipts by payment method
     *
     * GET /api/fee-receipts/by-method/{paymentMethod}
     *
     * @param paymentMethod Payment method (CASH, ONLINE, CHEQUE, CARD)
     * @return List of fee receipts
     */
    @GetMapping("/by-method/{paymentMethod}")
    public ResponseEntity<ApiResponse<List<FeeReceiptResponseDTO>>> getReceiptsByPaymentMethod(
            @PathVariable PaymentMethod paymentMethod) {

        log.info("GET /api/fee-receipts/by-method/{}", paymentMethod);

        List<FeeReceiptResponseDTO> receipts = feeReceiptService.getReceiptsByPaymentMethod(paymentMethod);

        ApiResponse<List<FeeReceiptResponseDTO>> response = ApiResponse.success(receipts);

        return ResponseEntity.ok(response);
    }

    /**
     * Get today's fee receipts
     *
     * GET /api/fee-receipts/today
     *
     * @return List of today's fee receipts
     */
    @GetMapping("/today")
    public ResponseEntity<ApiResponse<List<FeeReceiptResponseDTO>>> getTodayReceipts() {
        log.info("GET /api/fee-receipts/today");

        List<FeeReceiptResponseDTO> receipts = feeReceiptService.getTodayReceipts();

        ApiResponse<List<FeeReceiptResponseDTO>> response = ApiResponse.success(receipts);

        return ResponseEntity.ok(response);
    }

    /**
     * Get total collection for date range
     *
     * GET /api/fee-receipts/collection?startDate=2025-01-01&endDate=2025-01-31
     *
     * @param startDate Start date
     * @param endDate End date
     * @return Total amount collected
     */
    @GetMapping("/collection")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalCollection(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("GET /api/fee-receipts/collection?startDate={}&endDate={}", startDate, endDate);

        BigDecimal total = feeReceiptService.getTotalCollection(startDate, endDate);

        ApiResponse<BigDecimal> response = ApiResponse.success(total);

        return ResponseEntity.ok(response);
    }

    /**
     * Get total collection by payment method
     *
     * GET /api/fee-receipts/collection/by-method?method=CASH&startDate=2025-01-01&endDate=2025-01-31
     *
     * @param paymentMethod Payment method
     * @param startDate Start date
     * @param endDate End date
     * @return Total amount
     */
    @GetMapping("/collection/by-method")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalCollectionByMethod(
            @RequestParam PaymentMethod paymentMethod,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("GET /api/fee-receipts/collection/by-method?method={}&startDate={}&endDate={}",
                paymentMethod, startDate, endDate);

        BigDecimal total = feeReceiptService.getTotalCollectionByMethod(paymentMethod, startDate, endDate);

        ApiResponse<BigDecimal> response = ApiResponse.success(total);

        return ResponseEntity.ok(response);
    }

    /**
     * Get collection summary for date range
     *
     * GET /api/fee-receipts/collection/summary?startDate=2025-01-01&endDate=2025-01-31
     *
     * @param startDate Start date
     * @param endDate End date
     * @return Summary with collection breakdown by payment method
     */
    @GetMapping("/collection/summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCollectionSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("GET /api/fee-receipts/collection/summary?startDate={}&endDate={}", startDate, endDate);

        Map<String, Object> summary = feeReceiptService.getCollectionSummary(startDate, endDate);

        ApiResponse<Map<String, Object>> response = ApiResponse.success(summary);

        return ResponseEntity.ok(response);
    }

    /**
     * Count receipts for student
     *
     * GET /api/fee-receipts/count/{studentId}
     *
     * @param studentId Student ID
     * @return Number of receipts
     */
    @GetMapping("/count/{studentId}")
    public ResponseEntity<ApiResponse<Long>> countReceiptsForStudent(@PathVariable Long studentId) {
        log.info("GET /api/fee-receipts/count/{}", studentId);

        long count = feeReceiptService.countReceiptsForStudent(studentId);

        ApiResponse<Long> response = ApiResponse.success(count);

        return ResponseEntity.ok(response);
    }

    /**
     * Download receipt as PDF
     *
     * GET /api/fee-receipts/{id}/pdf
     *
     * @param id Receipt ID
     * @return PDF file (not implemented yet)
     */
    @GetMapping("/{id}/pdf")
    public ResponseEntity<ApiResponse<String>> downloadReceiptPDF(@PathVariable Long id) {
        log.info("GET /api/fee-receipts/{}/pdf", id);

        // Validate receipt exists
        feeReceiptService.getReceipt(id);

        // TODO: Implement PDF generation using iText or JasperReports
        ApiResponse<String> response = ApiResponse.success(
                "PDF generation not yet implemented. Receipt ID: " + id);

        return ResponseEntity.ok(response);
    }
}
