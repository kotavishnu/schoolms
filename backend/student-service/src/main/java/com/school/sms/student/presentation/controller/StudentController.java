package com.school.sms.student.presentation.controller;

import com.school.sms.student.application.service.StudentApplicationService;
import com.school.sms.student.presentation.dto.request.CreateStudentRequest;
import com.school.sms.student.presentation.dto.request.UpdateStudentRequest;
import com.school.sms.student.presentation.dto.response.PagedStudentResponse;
import com.school.sms.student.presentation.dto.response.StudentResponse;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for student operations.
 *
 * <p>Base path: /api/v1/students</p>
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Student Management", description = "APIs for managing students")
public class StudentController {

    private final StudentApplicationService studentService;

    /**
     * Creates a new student.
     *
     * @param request the create student request
     * @return the created student response (201 Created)
     */
    @PostMapping
    @Operation(summary = "Create a new student", description = "Registers a new student in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Student created successfully",
                     content = @Content(schema = @Schema(implementation = StudentResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "409", description = "Duplicate mobile or Aadhaar number"),
        @ApiResponse(responseCode = "422", description = "Invalid age (must be 3-18 years)")
    })
    public ResponseEntity<StudentResponse> createStudent(
        @Valid @RequestBody CreateStudentRequest request
    ) {
        log.info("POST /api/v1/students - Creating student with mobile: {}", request.getMobile());

        StudentResponse response = studentService.createStudent(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves a student by their student ID.
     *
     * @param studentId the student ID
     * @return the student response (200 OK)
     */
    @GetMapping("/{studentId}")
    @Operation(summary = "Get student by ID", description = "Retrieves detailed information about a specific student")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Student found",
                     content = @Content(schema = @Schema(implementation = StudentResponse.class))),
        @ApiResponse(responseCode = "404", description = "Student not found")
    })
    public ResponseEntity<StudentResponse> getStudent(
        @Parameter(description = "Student ID (format: STD-YYYYMMDD-NNNN)", example = "STD-20241206-0001")
        @PathVariable String studentId
    ) {
        log.info("GET /api/v1/students/{} - Retrieving student", studentId);

        StudentResponse response = studentService.getStudent(studentId);

        return ResponseEntity.ok(response);
    }

    /**
     * Updates an existing student.
     *
     * @param studentId the student ID
     * @param request the update student request
     * @return the updated student response (200 OK)
     */
    @PutMapping("/{studentId}")
    @Operation(summary = "Update student", description = "Updates student information (name, mobile, status)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Student updated successfully",
                     content = @Content(schema = @Schema(implementation = StudentResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Student not found"),
        @ApiResponse(responseCode = "409", description = "Duplicate mobile or version conflict")
    })
    public ResponseEntity<StudentResponse> updateStudent(
        @Parameter(description = "Student ID", example = "STD-20241206-0001")
        @PathVariable String studentId,
        @Valid @RequestBody UpdateStudentRequest request
    ) {
        log.info("PUT /api/v1/students/{} - Updating student", studentId);

        StudentResponse response = studentService.updateStudent(studentId, request);

        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a student.
     *
     * @param studentId the student ID
     * @return no content (204 No Content)
     */
    @DeleteMapping("/{studentId}")
    @Operation(summary = "Delete student", description = "Deletes a student from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Student deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Student not found")
    })
    public ResponseEntity<Void> deleteStudent(
        @Parameter(description = "Student ID", example = "STD-20241206-0001")
        @PathVariable String studentId
    ) {
        log.info("DELETE /api/v1/students/{} - Deleting student", studentId);

        studentService.deleteStudent(studentId);

        return ResponseEntity.noContent().build();
    }

    /**
     * Searches for students with pagination.
     *
     * @param lastName the last name to search for (optional)
     * @param fathersName the father's name to search for (optional)
     * @param status the student status (optional)
     * @param page the page number (default: 0)
     * @param size the page size (default: 20)
     * @param sort the sort field (default: createdAt)
     * @param direction the sort direction (default: DESC)
     * @return a paged response of students (200 OK)
     */
    @GetMapping
    @Operation(summary = "Search students", description = "Searches for students with optional filters and pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Students found",
                     content = @Content(schema = @Schema(implementation = PagedStudentResponse.class)))
    })
    public ResponseEntity<PagedStudentResponse> searchStudents(
        @Parameter(description = "Last name to search for (partial match, case-insensitive)")
        @RequestParam(required = false) String lastName,

        @Parameter(description = "Father's name to search for (partial match, case-insensitive)")
        @RequestParam(required = false) String fathersName,

        @Parameter(description = "Student status (ACTIVE or INACTIVE)")
        @RequestParam(required = false) String status,

        @Parameter(description = "Page number (0-indexed)")
        @RequestParam(defaultValue = "0") int page,

        @Parameter(description = "Number of items per page")
        @RequestParam(defaultValue = "20") int size,

        @Parameter(description = "Sort field (e.g., createdAt, firstName, lastName)")
        @RequestParam(defaultValue = "createdAt") String sort,

        @Parameter(description = "Sort direction (ASC or DESC)")
        @RequestParam(defaultValue = "DESC") String direction
    ) {
        log.info("GET /api/v1/students - Searching students (page: {}, size: {})", page, size);

        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        PagedStudentResponse response = studentService.searchStudents(lastName, fathersName, status, pageable);

        return ResponseEntity.ok(response);
    }
}
