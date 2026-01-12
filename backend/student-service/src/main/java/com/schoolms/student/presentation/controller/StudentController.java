package com.schoolms.student.presentation.controller;

import com.schoolms.student.application.service.StudentApplicationService;
import com.schoolms.student.domain.model.StudentStatus;
import com.schoolms.student.presentation.dto.request.CreateStudentRequest;
import com.schoolms.student.presentation.dto.request.UpdateStudentRequest;
import com.schoolms.student.presentation.dto.response.StudentListResponse;
import com.schoolms.student.presentation.dto.response.StudentResponse;
import com.schoolms.student.presentation.dto.response.StudentStatisticsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Student Controller (BE-023)
 * REST API endpoints for student operations
 */
@RestController
@RequestMapping("/api/v1/students")
@Tag(name = "Students", description = "Student management APIs")
public class StudentController {

    private final StudentApplicationService studentApplicationService;

    public StudentController(StudentApplicationService studentApplicationService) {
        this.studentApplicationService = studentApplicationService;
    }

    /**
     * GET /api/v1/students - List all students
     */
    @GetMapping
    @Operation(summary = "List all students", description = "Returns a list of students with optional search and status filter")
    public ResponseEntity<StudentListResponse> listStudents(
        @RequestParam(required = false) String search,
        @RequestParam(required = false) StudentStatus status
    ) {
        StudentListResponse response = studentApplicationService.listStudents(search, status);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/students/{id} - Get student by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get student by ID", description = "Returns a student by their student ID")
    public ResponseEntity<StudentResponse> getStudent(@PathVariable String id) {
        StudentResponse response = studentApplicationService.getStudent(id);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/students - Create student
     */
    @PostMapping
    @Operation(summary = "Create student", description = "Creates a new student record")
    public ResponseEntity<StudentResponse> createStudent(@Valid @RequestBody CreateStudentRequest request) {
        StudentResponse response = studentApplicationService.createStudent(request);

        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(response.id())
            .toUri();

        return ResponseEntity.created(location).body(response);
    }

    /**
     * PATCH /api/v1/students/{id} - Update student
     */
    @PatchMapping("/{id}")
    @Operation(summary = "Update student", description = "Updates an existing student record")
    public ResponseEntity<StudentResponse> updateStudent(
        @PathVariable String id,
        @Valid @RequestBody UpdateStudentRequest request
    ) {
        StudentResponse response = studentApplicationService.updateStudent(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/v1/students/{id} - Delete student
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete student", description = "Deletes a student record")
    public ResponseEntity<Void> deleteStudent(@PathVariable String id) {
        studentApplicationService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/v1/students/statistics - Get statistics
     */
    @GetMapping("/statistics")
    @Operation(summary = "Get student statistics", description = "Returns statistical data about students")
    public ResponseEntity<StudentStatisticsResponse> getStatistics() {
        StudentStatisticsResponse response = studentApplicationService.getStatistics();
        return ResponseEntity.ok(response);
    }
}
