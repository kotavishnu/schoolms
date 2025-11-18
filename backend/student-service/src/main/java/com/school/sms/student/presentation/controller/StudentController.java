package com.school.sms.student.presentation.controller;

import com.school.sms.student.application.dto.StudentDTO;
import com.school.sms.student.application.service.StudentManagementService;
import com.school.sms.student.application.service.StudentRegistrationService;
import com.school.sms.student.application.service.StudentSearchService;
import com.school.sms.student.application.service.StudentStatusService;
import com.school.sms.student.domain.valueobject.StudentStatus;
import com.school.sms.student.presentation.dto.CreateStudentRequest;
import com.school.sms.student.presentation.dto.StudentPageResponse;
import com.school.sms.student.presentation.dto.UpdateStatusRequest;
import com.school.sms.student.presentation.dto.UpdateStudentRequest;
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
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for Student operations.
 * Provides CRUD endpoints for student management.
 */
@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Student Management", description = "APIs for managing student records")
public class StudentController {

    private final StudentRegistrationService registrationService;
    private final StudentManagementService managementService;
    private final StudentSearchService searchService;
    private final StudentStatusService statusService;

    /**
     * Creates a new student.
     *
     * @param request the create student request
     * @param userId the user ID from header
     * @return created student DTO
     */
    @PostMapping
    @Operation(summary = "Create a new student", description = "Register a new student with validation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Student created successfully",
                    content = @Content(schema = @Schema(implementation = StudentDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or age validation failed",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Duplicate mobile number",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<StudentDTO> createStudent(
            @Valid @RequestBody CreateStudentRequest request,
            @RequestHeader(value = "X-User-ID", defaultValue = "SYSTEM") String userId) {

        log.info("Creating student: {} {}", request.getFirstName(), request.getLastName());
        StudentDTO student = registrationService.registerStudent(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(student);
    }

    /**
     * Gets a student by ID.
     *
     * @param studentId the student ID
     * @return student DTO
     */
    @GetMapping("/{studentId}")
    @Operation(summary = "Get student by ID", description = "Retrieve student details by student ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student found",
                    content = @Content(schema = @Schema(implementation = StudentDTO.class))),
            @ApiResponse(responseCode = "404", description = "Student not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<StudentDTO> getStudent(
            @Parameter(description = "Student ID (e.g., STU-2025-00001)")
            @PathVariable String studentId) {

        log.info("Getting student: {}", studentId);
        StudentDTO student = searchService.findById(studentId);
        return ResponseEntity.ok(student);
    }

    /**
     * Updates a student.
     *
     * @param studentId the student ID
     * @param request the update request
     * @param userId the user ID from header
     * @return updated student DTO
     */
    @PutMapping("/{studentId}")
    @Operation(summary = "Update student", description = "Update student profile with optimistic locking")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student updated successfully",
                    content = @Content(schema = @Schema(implementation = StudentDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Student not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Concurrent update or duplicate mobile",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<StudentDTO> updateStudent(
            @Parameter(description = "Student ID")
            @PathVariable String studentId,
            @Valid @RequestBody UpdateStudentRequest request,
            @RequestHeader(value = "X-User-ID", defaultValue = "SYSTEM") String userId) {

        log.info("Updating student: {}", studentId);
        StudentDTO student = managementService.updateStudent(studentId, request, userId);
        return ResponseEntity.ok(student);
    }

    /**
     * Deletes a student (soft delete).
     *
     * @param studentId the student ID
     */
    @DeleteMapping("/{studentId}")
    @Operation(summary = "Delete student", description = "Soft delete a student (sets status to INACTIVE)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Student deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Student not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<Void> deleteStudent(
            @Parameter(description = "Student ID")
            @PathVariable String studentId) {

        log.info("Deleting student: {}", studentId);
        managementService.deleteStudent(studentId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Searches students with filters and pagination.
     *
     * @param status student status filter
     * @param lastName last name filter (starts with)
     * @param page page number (0-indexed)
     * @param size page size
     * @param sort sort field and direction
     * @return paginated student list
     */
    @GetMapping
    @Operation(summary = "Search students", description = "Search and filter students with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Students retrieved successfully",
                    content = @Content(schema = @Schema(implementation = StudentPageResponse.class)))
    })
    public ResponseEntity<StudentPageResponse> searchStudents(
            @Parameter(description = "Filter by status (ACTIVE/INACTIVE)")
            @RequestParam(required = false) StudentStatus status,
            @Parameter(description = "Filter by last name (starts with)")
            @RequestParam(required = false) String lastName,
            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size (max 100)")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field and direction (e.g., createdAt,desc)")
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        log.info("Searching students: status={}, lastName={}, page={}, size={}", status, lastName, page, size);

        // Validate page size
        if (size > 100) {
            size = 100;
        }

        // Parse sort
        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        Page<StudentDTO> studentPage;

        // Apply filters
        if (status != null) {
            studentPage = searchService.findByStatus(status, pageable);
        } else if (lastName != null && !lastName.isBlank()) {
            studentPage = searchService.findByLastName(lastName, pageable);
        } else {
            studentPage = searchService.findAll(pageable);
        }

        // Build response
        StudentPageResponse response = StudentPageResponse.builder()
                .content(studentPage.getContent())
                .page(studentPage.getNumber())
                .size(studentPage.getSize())
                .totalElements(studentPage.getTotalElements())
                .totalPages(studentPage.getTotalPages())
                .first(studentPage.isFirst())
                .last(studentPage.isLast())
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Updates student status.
     *
     * @param studentId the student ID
     * @param request the status update request
     * @param userId the user ID from header
     * @return updated student DTO
     */
    @PatchMapping("/{studentId}/status")
    @Operation(summary = "Update student status", description = "Activate or deactivate a student")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status updated successfully",
                    content = @Content(schema = @Schema(implementation = StudentDTO.class))),
            @ApiResponse(responseCode = "404", description = "Student not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<StudentDTO> updateStatus(
            @Parameter(description = "Student ID")
            @PathVariable String studentId,
            @Valid @RequestBody UpdateStatusRequest request,
            @RequestHeader(value = "X-User-ID", defaultValue = "SYSTEM") String userId) {

        log.info("Updating student status: {} to {}", studentId, request.getStatus());
        StudentDTO student = statusService.updateStatus(studentId, request.getStatus(), userId);
        return ResponseEntity.ok(student);
    }
}
