package com.sms.student.presentation.controller;

import com.sms.student.application.service.StudentService;
import com.sms.student.presentation.dto.CreateStudentRequest;
import com.sms.student.presentation.dto.StudentDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for student management.
 * Provides endpoints for creating, reading, updating, and deleting students.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@Tag(name = "Student Management", description = "APIs for managing student information")
public class StudentController {

    private final StudentService studentService;

    /**
     * Create a new student.
     * POST /api/v1/students
     *
     * @param request the student creation request
     * @return created student DTO with HTTP 201
     */
    @PostMapping
    @Operation(summary = "Create a new student",
        description = "Create a new student with validation for age (3-18) and mobile uniqueness")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Student created successfully",
            content = @Content(schema = @Schema(implementation = StudentDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input or age out of range"),
        @ApiResponse(responseCode = "409", description = "Mobile number already exists")
    })
    public ResponseEntity<StudentDTO> createStudent(@Valid @RequestBody CreateStudentRequest request) {
        log.info("POST /api/v1/students - Creating new student");
        StudentDTO createdStudent = studentService.createStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStudent);
    }

    /**
     * Get student by student key.
     * GET /api/v1/students/{studentKey}
     *
     * @param studentKey the student business key
     * @return the student DTO
     */
    @GetMapping("/{studentKey}")
    @Operation(summary = "Get student by key",
        description = "Retrieve student details using the student key (e.g., STU-2025-0001)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Student found",
            content = @Content(schema = @Schema(implementation = StudentDTO.class))),
        @ApiResponse(responseCode = "404", description = "Student not found")
    })
    public ResponseEntity<StudentDTO> getStudent(
        @PathVariable String studentKey) {
        log.info("GET /api/v1/students/{} - Fetching student", studentKey);
        StudentDTO student = studentService.getStudent(studentKey);
        return ResponseEntity.ok(student);
    }

    /**
     * Update an existing student.
     * PUT /api/v1/students/{studentKey}
     *
     * @param studentKey the student business key
     * @param request the update request
     * @return updated student DTO
     */
    @PutMapping("/{studentKey}")
    @Operation(summary = "Update student",
        description = "Update student information (firstName, lastName, mobile, status)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Student updated successfully",
            content = @Content(schema = @Schema(implementation = StudentDTO.class))),
        @ApiResponse(responseCode = "404", description = "Student not found"),
        @ApiResponse(responseCode = "409", description = "Mobile number already exists")
    })
    public ResponseEntity<StudentDTO> updateStudent(
        @PathVariable String studentKey,
        @Valid @RequestBody CreateStudentRequest request) {
        log.info("PUT /api/v1/students/{} - Updating student", studentKey);
        StudentDTO updatedStudent = studentService.updateStudent(studentKey, request);
        return ResponseEntity.ok(updatedStudent);
    }

    /**
     * Delete a student.
     * DELETE /api/v1/students/{studentKey}
     *
     * @param studentKey the student business key
     * @return HTTP 204 No Content
     */
    @DeleteMapping("/{studentKey}")
    @Operation(summary = "Delete student",
        description = "Remove a student from the system")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Student deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Student not found")
    })
    public ResponseEntity<Void> deleteStudent(
        @PathVariable String studentKey) {
        log.info("DELETE /api/v1/students/{} - Deleting student", studentKey);
        studentService.deleteStudent(studentKey);
        return ResponseEntity.noContent().build();
    }

    /**
     * Search students.
     * GET /api/v1/students?search={term}&page={page}&size={size}
     *
     * @param search the search term (searches lastName and fatherNameOrGuardian)
     * @param page the page number (default 0)
     * @param size the page size (default 20)
     * @return page of students matching the search term
     */
    @GetMapping
    @Operation(summary = "Search students",
        description = "Search students by lastName or fatherNameOrGuardian with pagination")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Students found",
            content = @Content(schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters")
    })
    public ResponseEntity<Page<StudentDTO>> searchStudents(
        @RequestParam(value = "search", defaultValue = "") String search,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "20") int size) {
        log.info("GET /api/v1/students?search={}&page={}&size={} - Searching students", search, page, size);

        // Validate pagination parameters
        if (page < 0) {
            page = 0;
        }
        if (size <= 0 || size > 100) {
            size = 20;
        }

        Page<StudentDTO> result = studentService.searchStudents(search, page, size);
        return ResponseEntity.ok(result);
    }
}
