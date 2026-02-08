package com.school.student.presentation.controller;

import com.school.student.application.service.StudentService;
import com.school.student.domain.model.StudentStatus;
import com.school.student.presentation.dto.StudentRequestDTO;
import com.school.student.presentation.dto.StudentResponseDTO;
import com.school.student.presentation.dto.StudentUpdateRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * REST controller for student management operations.
 * Implements endpoints matching OpenAPI specification.
 *
 * <p>Base path: /api/v1/students
 * All endpoints support correlation ID header (X-Correlation-ID).
 */
@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Students", description = "Student management operations")
@CrossOrigin(
    origins = {"http://localhost:3000", "http://localhost:5173"},
    allowedHeaders = "*",
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
    allowCredentials = "true"
)
public class StudentController {

    private final StudentService studentService;

    /**
     * Register a new student.
     *
     * @param requestDTO student registration data
     * @return created student with 201 status and Location header
     */
    @PostMapping
    @Operation(
        summary = "Register a new student",
        description = "Creates a new student record with auto-generated student ID. Validates business rules (age 3-18, mobile uniqueness, guardian required)."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Student registered successfully",
            content = @Content(schema = @Schema(implementation = StudentResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input or business rule violation"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<StudentResponseDTO> registerStudent(
            @Valid @RequestBody StudentRequestDTO requestDTO) {

        log.info("POST /api/v1/students - Registering student: {} {}",
                requestDTO.firstName(), requestDTO.lastName());

        StudentResponseDTO response = studentService.registerStudent(requestDTO);

        // Build Location header
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{studentId}")
                .buildAndExpand(response.studentId())
                .toUri();

        log.info("Student registered successfully: {}", response.studentId());

        return ResponseEntity.created(location).body(response);
    }

    /**
     * Get student by ID.
     *
     * @param studentId the unique business key
     * @return student data with 200 status
     */
    @GetMapping("/{studentId}")
    @Operation(
        summary = "Get student by ID",
        description = "Retrieves student details by their unique student ID (e.g., STD-20260203-0001)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Student found",
            content = @Content(schema = @Schema(implementation = StudentResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Student not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<StudentResponseDTO> getStudentById(
            @Parameter(description = "Student ID (e.g., STD-20260203-0001)", required = true)
            @PathVariable String studentId) {

        log.debug("GET /api/v1/students/{} - Fetching student", studentId);

        StudentResponseDTO response = studentService.getStudentById(studentId);
        return ResponseEntity.ok(response);
    }

    /**
     * Update student profile.
     *
     * @param studentId the student ID
     * @param updateDTO update data with version for optimistic locking
     * @return updated student with 200 status
     */
    @PutMapping("/{studentId}")
    @Operation(
        summary = "Update student profile",
        description = "Updates allowed student fields. Requires version for optimistic locking. Returns 409 on version mismatch."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Student updated successfully",
            content = @Content(schema = @Schema(implementation = StudentResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Student not found"),
        @ApiResponse(responseCode = "409", description = "Optimistic locking conflict - version mismatch"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<StudentResponseDTO> updateStudent(
            @Parameter(description = "Student ID", required = true)
            @PathVariable String studentId,
            @Valid @RequestBody StudentUpdateRequestDTO updateDTO) {

        log.info("PUT /api/v1/students/{} - Updating student", studentId);

        StudentResponseDTO response = studentService.updateStudent(studentId, updateDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete student by ID.
     *
     * @param studentId the student ID
     * @return 204 No Content on success
     */
    @DeleteMapping("/{studentId}")
    @Operation(
        summary = "Delete student",
        description = "Permanently deletes a student record and associated enrollments (cascade delete)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Student deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Student not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteStudent(
            @Parameter(description = "Student ID", required = true)
            @PathVariable String studentId) {

        log.info("DELETE /api/v1/students/{} - Deleting student", studentId);

        studentService.deleteStudent(studentId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Search/list students with optional filters and pagination.
     *
     * @param lastName optional last name filter (partial match)
     * @param status optional status filter (ACTIVE/INACTIVE)
     * @param page page number (0-based)
     * @param size page size (default 20, max 100)
     * @param sortBy field to sort by (default: lastName)
     * @param sortDirection sort direction (ASC/DESC, default: ASC)
     * @return paginated list of students
     */
    @GetMapping
    @Operation(
        summary = "List/search students",
        description = "Retrieves paginated list of students with optional filters (lastName, status). Supports sorting."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Students retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid query parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<StudentResponseDTO>> searchStudents(
            @Parameter(description = "Last name filter (partial match, case-insensitive)")
            @RequestParam(required = false) String lastName,

            @Parameter(description = "Status filter (ACTIVE or INACTIVE)")
            @RequestParam(required = false) StudentStatus status,

            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size (max 100)")
            @RequestParam(defaultValue = "20") int size,

            @Parameter(description = "Field to sort by")
            @RequestParam(defaultValue = "lastName") String sortBy,

            @Parameter(description = "Sort direction (ASC or DESC)")
            @RequestParam(defaultValue = "ASC") String sortDirection) {

        log.debug("GET /api/v1/students - Searching students: lastName={}, status={}, page={}, size={}",
                lastName, status, page, size);

        // Validate and limit page size
        size = Math.min(size, 100);

        // Create pageable with sorting
        Sort.Direction direction = sortDirection.equalsIgnoreCase("DESC")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        // Delegate to appropriate service method based on filters
        Page<StudentResponseDTO> result;
        if (lastName != null && !lastName.isBlank()) {
            result = studentService.searchByLastName(lastName.trim(), pageable);
        } else if (status != null) {
            result = studentService.findByStatus(status, pageable);
        } else {
            result = studentService.getAllStudents(pageable);
        }

        return ResponseEntity.ok(result);
    }
}
