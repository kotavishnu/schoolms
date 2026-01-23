package com.school.student.controller;

import com.school.student.domain.entity.StudentStatus;
import com.school.student.dto.request.StudentRequest;
import com.school.student.dto.request.StudentUpdateRequest;
import com.school.student.dto.response.StudentResponse;
import com.school.student.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

/**
 * Student Controller
 *
 * REST API endpoints for student management.
 * Base path: /api/v1/students
 *
 * All endpoints return RFC 7807 Problem Details for errors.
 */
@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Student Management", description = "APIs for student registration, update, search, and deletion")
public class StudentController {

    private final StudentService studentService;

    /**
     * Search students with optional filters
     *
     * GET /api/v1/students?lastName=Smith&status=ACTIVE&page=0&size=20
     *
     * @param lastName Optional last name filter (partial match, case-insensitive)
     * @param status Optional status filter (ACTIVE/INACTIVE)
     * @param pageable Pagination parameters (page, size, sort)
     * @return Page of students
     */
    @GetMapping
    @Operation(summary = "Search students", description = "Search students by last name and/or status with pagination")
    public ResponseEntity<Page<StudentResponse>> searchStudents(
        @RequestParam(required = false) String lastName,
        @RequestParam(required = false) StudentStatus status,
        @PageableDefault(size = 20, sort = "createdAt") Pageable pageable
    ) {
        log.debug("GET /api/v1/students - lastName={}, status={}, page={}", lastName, status, pageable.getPageNumber());

        Page<StudentResponse> students = studentService.searchStudents(lastName, status, pageable);

        return ResponseEntity.ok(students);
    }

    /**
     * Get student by ID
     *
     * GET /api/v1/students/{studentId}
     *
     * @param studentId Student business key (STD-YYYYMMDD-NNNN)
     * @return Student details
     * @throws com.school.student.exception.StudentNotFoundException if not found (404)
     */
    @GetMapping("/{studentId}")
    @Operation(summary = "Get student by ID", description = "Retrieve student details by student ID")
    public ResponseEntity<StudentResponse> getStudent(
        @PathVariable String studentId
    ) {
        log.debug("GET /api/v1/students/{}", studentId);

        StudentResponse student = studentService.getStudentById(studentId);

        return ResponseEntity.ok(student);
    }

    /**
     * Register new student
     *
     * POST /api/v1/students
     *
     * @param request Student registration data
     * @return Created student with generated studentId
     * @throws com.school.student.exception.ValidationException if validation fails (400)
     */
    @PostMapping
    @Operation(summary = "Register student", description = "Create a new student record")
    public ResponseEntity<StudentResponse> registerStudent(
        @Valid @RequestBody StudentRequest request
    ) {
        log.info("POST /api/v1/students - {} {}", request.getFirstName(), request.getLastName());

        StudentResponse created = studentService.registerStudent(request);

        URI location = URI.create("/api/v1/students/" + created.getStudentId());

        return ResponseEntity.created(location).body(created);
    }

    /**
     * Update student
     *
     * PUT /api/v1/students/{studentId}
     *
     * Only editable fields: firstName, lastName, mobile, status
     * Immutable fields: dateOfBirth, email, aadhaarNumber, etc.
     *
     * @param studentId Student to update
     * @param request Update data (must include version for optimistic locking)
     * @return Updated student
     * @throws com.school.student.exception.StudentNotFoundException if not found (404)
     * @throws com.school.student.exception.ValidationException if validation fails (400)
     * @throws jakarta.persistence.OptimisticLockException if version conflict (409)
     */
    @PutMapping("/{studentId}")
    @Operation(summary = "Update student", description = "Update student information (editable fields only)")
    public ResponseEntity<StudentResponse> updateStudent(
        @PathVariable String studentId,
        @Valid @RequestBody StudentUpdateRequest request
    ) {
        log.info("PUT /api/v1/students/{}", studentId);

        StudentResponse updated = studentService.updateStudent(studentId, request);

        return ResponseEntity.ok(updated);
    }

    /**
     * Delete student
     *
     * DELETE /api/v1/students/{studentId}
     *
     * @param studentId Student to delete
     * @return 204 No Content
     * @throws com.school.student.exception.StudentNotFoundException if not found (404)
     */
    @DeleteMapping("/{studentId}")
    @Operation(summary = "Delete student", description = "Delete a student record")
    public ResponseEntity<Void> deleteStudent(
        @PathVariable String studentId
    ) {
        log.info("DELETE /api/v1/students/{}", studentId);

        studentService.deleteStudent(studentId);

        return ResponseEntity.noContent().build();
    }

    /**
     * Get student statistics
     *
     * GET /api/v1/students/statistics
     *
     * Returns dashboard statistics:
     * - total: Total number of students
     * - active: Number of active students
     * - inactive: Number of inactive students
     *
     * @return Statistics map
     */
    @GetMapping("/statistics")
    @Operation(summary = "Get statistics", description = "Get student count statistics for dashboard")
    public ResponseEntity<Map<String, Long>> getStatistics() {
        log.debug("GET /api/v1/students/statistics");

        Map<String, Long> stats = studentService.getStatistics();

        return ResponseEntity.ok(stats);
    }
}
