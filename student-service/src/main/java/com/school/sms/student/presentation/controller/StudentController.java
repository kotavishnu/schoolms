package com.school.sms.student.presentation.controller;

import com.school.sms.student.application.dto.*;
import com.school.sms.student.application.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for student operations.
 */
@RestController
@RequestMapping("/api/v1/students")
@Tag(name = "Student Management", description = "APIs for managing students")
public class StudentController {

    private static final Logger log = LoggerFactory.getLogger(StudentController.class);

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    @Operation(summary = "Create a new student", description = "Register a new student in the system")
    public ResponseEntity<StudentResponse> createStudent(@Valid @RequestBody CreateStudentRequest request) {
        log.info("Received request to create student: {} {}", request.getFirstName(), request.getLastName());
        StudentResponse response = studentService.createStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "List all students", description = "Get paginated list of students with optional search criteria")
    public ResponseEntity<StudentPageResponse> listStudents(
            @ParameterObject @ModelAttribute StudentSearchCriteria criteria,
            @ParameterObject Pageable pageable) {
        log.debug("Listing students with criteria: {}", criteria);
        StudentPageResponse response = studentService.searchStudents(criteria, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get student by database ID")
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable Long id) {
        log.debug("Fetching student by ID: {}", id);
        StudentResponse response = studentService.getStudentById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/student-id/{studentId}")
    @Operation(summary = "Get student by Student ID", description = "Fetch student using Student ID format STU-YYYY-XXXXX")
    public ResponseEntity<StudentResponse> getStudentByStudentId(@PathVariable String studentId) {
        log.debug("Fetching student by StudentId: {}", studentId);
        StudentResponse response = studentService.getStudentByStudentId(studentId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update student profile", description = "Update editable fields: firstName, lastName, mobile")
    public ResponseEntity<StudentResponse> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStudentRequest request) {
        log.info("Updating student ID: {}", id);
        StudentResponse response = studentService.updateStudent(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update student status", description = "Change student status (ACTIVE, INACTIVE, GRADUATED, TRANSFERRED)")
    public ResponseEntity<StudentResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request) {
        log.info("Updating status for student ID: {}", id);
        StudentResponse response = studentService.updateStatus(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete student", description = "Soft delete student by setting status to INACTIVE")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        log.info("Deleting student ID: {}", id);
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}
