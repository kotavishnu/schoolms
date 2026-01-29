package com.school.student.controller;

import com.school.student.controller.dto.request.EnrollmentRequest;
import com.school.student.controller.dto.response.EnrollmentResponse;
import com.school.student.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API Controller for Enrollment Management
 * Nested under /api/v1/students/{studentId}/enrollment-history
 */
@RestController
@RequestMapping("/api/v1/students/{studentId}/enrollment-history")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Enrollment Management", description = "APIs for managing student enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    /**
     * Get enrollment history for student
     * GET /api/v1/students/{studentId}/enrollment-history
     */
    @GetMapping
    @Operation(summary = "Get enrollment history", description = "Retrieves all enrollments for a student")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Enrollment history retrieved"),
        @ApiResponse(responseCode = "404", description = "Student not found")
    })
    public ResponseEntity<List<EnrollmentResponse>> getEnrollmentHistory(
        @Parameter(description = "Student ID", example = "STD-20260128-0001")
        @PathVariable String studentId
    ) {
        log.debug("REST: Fetching enrollment history for student: {}", studentId);
        List<EnrollmentResponse> enrollments = enrollmentService.getEnrollmentHistory(studentId);
        return ResponseEntity.ok(enrollments);
    }

    /**
     * Create new enrollment
     * POST /api/v1/students/{studentId}/enrollment-history
     */
    @PostMapping
    @Operation(summary = "Create enrollment", description = "Creates new enrollment for student")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Enrollment created successfully"),
        @ApiResponse(responseCode = "404", description = "Student not found"),
        @ApiResponse(responseCode = "409", description = "Duplicate enrollment for academic year")
    })
    public ResponseEntity<EnrollmentResponse> createEnrollment(
        @PathVariable String studentId,
        @Valid @RequestBody EnrollmentRequest request
    ) {
        log.info("REST: Creating enrollment for student: {}", studentId);
        EnrollmentResponse response = enrollmentService.createEnrollment(studentId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
