package com.school.management.controller;

import com.school.management.dto.request.FeeMasterRequestDTO;
import com.school.management.dto.response.ApiResponse;
import com.school.management.dto.response.FeeMasterResponseDTO;
import com.school.management.model.FeeType;
import com.school.management.service.FeeMasterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Fee Master management operations.
 *
 * Provides endpoints for CRUD operations, fee type filtering, and applicability checks.
 *
 * @author School Management Team
 */
@RestController
@RequestMapping("/api/fee-masters")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class FeeMasterController {

    private final FeeMasterService feeMasterService;

    /**
     * Create a new fee master
     *
     * POST /api/fee-masters
     *
     * @param requestDTO Fee master data
     * @return Created fee master with HTTP 201
     */
    @PostMapping
    public ResponseEntity<ApiResponse<FeeMasterResponseDTO>> createFeeMaster(
            @Valid @RequestBody FeeMasterRequestDTO requestDTO) {

        log.info("POST /api/fee-masters - Creating fee master: {}", requestDTO.getFeeType());

        FeeMasterResponseDTO feeMaster = feeMasterService.createFeeMaster(requestDTO);

        ApiResponse<FeeMasterResponseDTO> response = ApiResponse.success(
                "Fee master created successfully", feeMaster);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get fee master by ID
     *
     * GET /api/fee-masters/{id}
     *
     * @param id Fee master ID
     * @return Fee master details
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FeeMasterResponseDTO>> getFeeMaster(@PathVariable Long id) {
        log.info("GET /api/fee-masters/{}", id);

        FeeMasterResponseDTO feeMaster = feeMasterService.getFeeMaster(id);

        ApiResponse<FeeMasterResponseDTO> response = ApiResponse.success(feeMaster);

        return ResponseEntity.ok(response);
    }

    /**
     * Get all fee masters or filter by academic year
     *
     * GET /api/fee-masters
     * GET /api/fee-masters?academicYear=2024-2025
     *
     * @param academicYear Optional academic year filter
     * @return List of fee masters
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<FeeMasterResponseDTO>>> getAllFeeMasters(
            @RequestParam(required = false) String academicYear) {

        log.info("GET /api/fee-masters - Academic year filter: {}", academicYear);

        List<FeeMasterResponseDTO> feeMasters = academicYear != null
                ? feeMasterService.getFeeMastersByAcademicYear(academicYear)
                : feeMasterService.getAllFeeMasters();

        ApiResponse<List<FeeMasterResponseDTO>> response = ApiResponse.success(feeMasters);

        return ResponseEntity.ok(response);
    }

    /**
     * Get fee masters by fee type
     *
     * GET /api/fee-masters/by-type/{feeType}
     *
     * @param feeType Fee type (TUITION, LIBRARY, etc.)
     * @param activeOnly Optional flag to get only active fee masters
     * @return List of fee masters for the type
     */
    @GetMapping("/by-type/{feeType}")
    public ResponseEntity<ApiResponse<List<FeeMasterResponseDTO>>> getFeeMastersByType(
            @PathVariable FeeType feeType,
            @RequestParam(required = false, defaultValue = "false") boolean activeOnly) {

        log.info("GET /api/fee-masters/by-type/{}?activeOnly={}", feeType, activeOnly);

        List<FeeMasterResponseDTO> feeMasters = activeOnly
                ? feeMasterService.getActiveFeeMastersByType(feeType)
                : feeMasterService.getFeeMastersByType(feeType);

        ApiResponse<List<FeeMasterResponseDTO>> response = ApiResponse.success(feeMasters);

        return ResponseEntity.ok(response);
    }

    /**
     * Get active fee masters for academic year
     *
     * GET /api/fee-masters/active?academicYear=2024-2025
     *
     * @param academicYear Optional academic year filter
     * @return List of active fee masters
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<FeeMasterResponseDTO>>> getActiveFeeMasters(
            @RequestParam(required = false) String academicYear) {

        log.info("GET /api/fee-masters/active?academicYear={}", academicYear);

        List<FeeMasterResponseDTO> feeMasters = academicYear != null
                ? feeMasterService.getActiveFeeMastersByAcademicYear(academicYear)
                : feeMasterService.getActiveFeeMasters();

        ApiResponse<List<FeeMasterResponseDTO>> response = ApiResponse.success(feeMasters);

        return ResponseEntity.ok(response);
    }

    /**
     * Get currently applicable fee masters
     *
     * GET /api/fee-masters/applicable
     *
     * @return List of currently applicable fee masters
     */
    @GetMapping("/applicable")
    public ResponseEntity<ApiResponse<List<FeeMasterResponseDTO>>> getCurrentlyApplicableFeeMasters() {
        log.info("GET /api/fee-masters/applicable");

        List<FeeMasterResponseDTO> feeMasters = feeMasterService.getCurrentlyApplicableFeeMasters();

        ApiResponse<List<FeeMasterResponseDTO>> response = ApiResponse.success(feeMasters);

        return ResponseEntity.ok(response);
    }

    /**
     * Get latest applicable fee master for a specific fee type
     *
     * GET /api/fee-masters/latest/{feeType}
     *
     * @param feeType Fee type
     * @return Latest applicable fee master
     */
    @GetMapping("/latest/{feeType}")
    public ResponseEntity<ApiResponse<FeeMasterResponseDTO>> getLatestApplicableFeeMaster(
            @PathVariable FeeType feeType) {

        log.info("GET /api/fee-masters/latest/{}", feeType);

        FeeMasterResponseDTO feeMaster = feeMasterService.getLatestApplicableFeeMaster(feeType);

        ApiResponse<FeeMasterResponseDTO> response = ApiResponse.success(feeMaster);

        return ResponseEntity.ok(response);
    }

    /**
     * Update fee master
     *
     * PUT /api/fee-masters/{id}
     *
     * @param id Fee master ID
     * @param requestDTO Updated fee master data
     * @return Updated fee master
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FeeMasterResponseDTO>> updateFeeMaster(
            @PathVariable Long id,
            @Valid @RequestBody FeeMasterRequestDTO requestDTO) {

        log.info("PUT /api/fee-masters/{}", id);

        FeeMasterResponseDTO feeMaster = feeMasterService.updateFeeMaster(id, requestDTO);

        ApiResponse<FeeMasterResponseDTO> response = ApiResponse.success(
                "Fee master updated successfully", feeMaster);

        return ResponseEntity.ok(response);
    }

    /**
     * Deactivate fee master
     *
     * PATCH /api/fee-masters/{id}/deactivate
     *
     * @param id Fee master ID
     * @return Updated fee master
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<FeeMasterResponseDTO>> deactivateFeeMaster(@PathVariable Long id) {
        log.info("PATCH /api/fee-masters/{}/deactivate", id);

        FeeMasterResponseDTO feeMaster = feeMasterService.deactivateFeeMaster(id);

        ApiResponse<FeeMasterResponseDTO> response = ApiResponse.success(
                "Fee master deactivated successfully", feeMaster);

        return ResponseEntity.ok(response);
    }

    /**
     * Activate fee master
     *
     * PATCH /api/fee-masters/{id}/activate
     *
     * @param id Fee master ID
     * @return Updated fee master
     */
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<FeeMasterResponseDTO>> activateFeeMaster(@PathVariable Long id) {
        log.info("PATCH /api/fee-masters/{}/activate", id);

        FeeMasterResponseDTO feeMaster = feeMasterService.activateFeeMaster(id);

        ApiResponse<FeeMasterResponseDTO> response = ApiResponse.success(
                "Fee master activated successfully", feeMaster);

        return ResponseEntity.ok(response);
    }

    /**
     * Delete fee master
     *
     * DELETE /api/fee-masters/{id}
     *
     * @param id Fee master ID
     * @return Success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFeeMaster(@PathVariable Long id) {
        log.info("DELETE /api/fee-masters/{}", id);

        feeMasterService.deleteFeeMaster(id);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Fee master deleted successfully")
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Count active fee masters for academic year
     *
     * GET /api/fee-masters/count?academicYear=2024-2025
     *
     * @param academicYear Academic year
     * @return Count of active fee masters
     */
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> countActiveFeeMasters(
            @RequestParam String academicYear) {

        log.info("GET /api/fee-masters/count?academicYear={}", academicYear);

        long count = feeMasterService.countActiveFeeMasters(academicYear);

        ApiResponse<Long> response = ApiResponse.success(count);

        return ResponseEntity.ok(response);
    }
}
