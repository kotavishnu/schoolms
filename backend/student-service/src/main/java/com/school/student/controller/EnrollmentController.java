package com.school.student.controller;

import com.school.student.dto.request.EnrollmentRequest;
import com.school.student.dto.response.EnrollmentResponse;
import com.school.student.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * Enrollment Controller
 *
 * REST API endpoints for enrollment history management.
 * Base path: /api/v1/students/{studentId}/enrollments
 */
@RestController
@RequestMapping("/api/v1/students/{studentId}/enrollments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Enrollment Management", description = "APIs for managing student enrollment history")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    /**
     * Get enrollment history for a student
     *
     * GET /api/v1/students/{studentId}/enrollments
     */
    @GetMapping
    @Operation(summary = "Get enrollment history", description = "Retrieve all enrollments for a student")
    public ResponseEntity<List<EnrollmentResponse>> getEnrollmentHistory(
        @PathVariable String studentId
    ) {
        log.debug("GET /api/v1/students/{}/enrollments", studentId);

        List<EnrollmentResponse> enrollments = enrollmentService.getEnrollmentHistory(studentId);

        return ResponseEntity.ok(enrollments);
    }

    /**
     * Create new enrollment for a student
     *
     * POST /api/v1/students/{studentId}/enrollments
     */
    @PostMapping
    @Operation(summary = "Create enrollment", description = "Add a new enrollment record for a student")
    public ResponseEntity<EnrollmentResponse> createEnrollment(
        @PathVariable String studentId,
        @Valid @RequestBody EnrollmentRequest request
    ) {
        log.info("POST /api/v1/students/{}/enrollments", studentId);

        EnrollmentResponse created = enrollmentService.createEnrollment(studentId, request);

        URI location = URI.create("/api/v1/students/" + studentId + "/enrollments/" + created.getId());

        return ResponseEntity.created(location).body(created);
    }
}
