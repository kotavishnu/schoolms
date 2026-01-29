package com.school.student.controller;

import com.school.student.controller.dto.request.StudentRequest;
import com.school.student.controller.dto.request.StudentUpdateRequest;
import com.school.student.controller.dto.response.StudentResponse;
import com.school.student.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST API Controller for Student Management
 * Provides 6 endpoints for CRUD operations and utilities
 */
@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Student Management", description = "APIs for managing students")
public class StudentController {

    private final StudentService studentService;

    /**
     * Create new student
     * POST /api/v1/students
     */
    @PostMapping
    @Operation(summary = "Register new student", description = "Creates a new student record with validation")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Student created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Duplicate mobile or Aadhaar number")
    })
    public ResponseEntity<StudentResponse> createStudent(
        @Valid @RequestBody StudentRequest request
    ) {
        log.info("REST: Creating student with mobile: {}", request.getMobile());
        StudentResponse response = studentService.registerStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get student by ID
     * GET /api/v1/students/{studentId}
     */
    @GetMapping("/{studentId}")
    @Operation(summary = "Get student by ID", description = "Retrieves student details by unique student ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Student found"),
        @ApiResponse(responseCode = "404", description = "Student not found")
    })
    public ResponseEntity<StudentResponse> getStudent(
        @Parameter(description = "Student ID (format: STD-YYYYMMDD-NNNN)", example = "STD-20260128-0001")
        @PathVariable String studentId
    ) {
        log.debug("REST: Fetching student: {}", studentId);
        StudentResponse response = studentService.getStudentById(studentId);
        return ResponseEntity.ok(response);
    }

    /**
     * Search students with filters
     * GET /api/v1/students?lastName=&status=
     */
    @GetMapping
    @Operation(summary = "Search students", description = "Search students with optional filters and pagination")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Students retrieved successfully")
    })
    public ResponseEntity<Page<StudentResponse>> searchStudents(
        @Parameter(description = "Filter by last name (partial match)")
        @RequestParam(required = false) String lastName,
        @Parameter(description = "Filter by status (ACTIVE/INACTIVE)")
        @RequestParam(required = false) String status,
        @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        log.debug("REST: Searching students with lastName={}, status={}", lastName, status);
        Page<StudentResponse> students = studentService.searchStudents(lastName, status, pageable);
        return ResponseEntity.ok(students);
    }

    /**
     * Update student (only editable fields)
     * PUT /api/v1/students/{studentId}
     */
    @PutMapping("/{studentId}")
    @Operation(summary = "Update student", description = "Updates editable fields: firstName, lastName, mobile, status")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Student updated successfully"),
        @ApiResponse(responseCode = "404", description = "Student not found"),
        @ApiResponse(responseCode = "409", description = "Optimistic locking conflict")
    })
    public ResponseEntity<StudentResponse> updateStudent(
        @PathVariable String studentId,
        @Valid @RequestBody StudentUpdateRequest request
    ) {
        log.info("REST: Updating student: {}", studentId);
        StudentResponse response = studentService.updateStudent(studentId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete student
     * DELETE /api/v1/students/{studentId}
     */
    @DeleteMapping("/{studentId}")
    @Operation(summary = "Delete student", description = "Soft or hard delete student record")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Student deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Student not found")
    })
    public ResponseEntity<Void> deleteStudent(@PathVariable String studentId) {
        log.info("REST: Deleting student: {}", studentId);
        studentService.deleteStudent(studentId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Validate phone number availability
     * POST /api/v1/students/validate-phone
     */
    @PostMapping("/validate-phone")
    @Operation(summary = "Validate phone availability", description = "Checks if mobile number is available")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Validation result returned")
    })
    public ResponseEntity<Map<String, Boolean>> validatePhone(
        @RequestBody Map<String, String> request
    ) {
        String mobile = request.get("mobile");
        log.debug("REST: Validating phone: {}", mobile);
        boolean available = studentService.validatePhone(mobile);
        return ResponseEntity.ok(Map.of("available", available));
    }
}
