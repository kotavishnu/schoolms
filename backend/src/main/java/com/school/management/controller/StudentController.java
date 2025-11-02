package com.school.management.controller;

import com.school.management.dto.request.StudentRequestDTO;
import com.school.management.dto.response.ApiResponse;
import com.school.management.dto.response.StudentResponseDTO;
import com.school.management.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Student management operations.
 *
 * Provides endpoints for CRUD operations, search, and student management.
 *
 * @author School Management Team
 */
@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class StudentController {

    private final StudentService studentService;

    /**
     * Create a new student
     *
     * POST /api/students
     *
     * @param requestDTO Student registration data
     * @return Created student with HTTP 201
     */
    @PostMapping
    public ResponseEntity<ApiResponse<StudentResponseDTO>> createStudent(
            @Valid @RequestBody StudentRequestDTO requestDTO) {

        log.info("POST /api/students - Creating student: {} {}",
                 requestDTO.getFirstName(), requestDTO.getLastName());

        StudentResponseDTO student = studentService.createStudent(requestDTO);

        ApiResponse<StudentResponseDTO> response = ApiResponse.success(
                "Student registered successfully", student);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get student by ID
     *
     * GET /api/students/{id}
     *
     * @param id Student ID
     * @return Student details
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> getStudent(@PathVariable Long id) {
        log.info("GET /api/students/{}", id);

        StudentResponseDTO student = studentService.getStudent(id);

        ApiResponse<StudentResponseDTO> response = ApiResponse.success(student);

        return ResponseEntity.ok(response);
    }

    /**
     * Get all students or filter by class
     *
     * GET /api/students
     * GET /api/students?classId=5
     *
     * @param classId Optional class ID filter
     * @return List of students
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<StudentResponseDTO>>> getAllStudents(
            @RequestParam(required = false) Long classId) {

        log.info("GET /api/students - ClassId filter: {}", classId);

        List<StudentResponseDTO> students = studentService.getAllStudents(classId);

        ApiResponse<List<StudentResponseDTO>> response = ApiResponse.success(students);

        return ResponseEntity.ok(response);
    }

    /**
     * Search students by name
     *
     * GET /api/students/search?q=rajesh
     *
     * @param query Search query (minimum 2 characters)
     * @return List of matching students
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<StudentResponseDTO>>> searchStudents(
            @RequestParam("q") String query) {

        log.info("GET /api/students/search?q={}", query);

        List<StudentResponseDTO> students = studentService.searchStudents(query);

        ApiResponse<List<StudentResponseDTO>> response = ApiResponse.success(students);

        return ResponseEntity.ok(response);
    }

    /**
     * Autocomplete search for students (name or mobile)
     *
     * GET /api/students/autocomplete?q=raj
     *
     * @param query Search query
     * @return Limited list of matching students
     */
    @GetMapping("/autocomplete")
    public ResponseEntity<ApiResponse<List<StudentResponseDTO>>> autocompleteSearch(
            @RequestParam("q") String query) {

        log.info("GET /api/students/autocomplete?q={}", query);

        List<StudentResponseDTO> students = studentService.searchForAutocomplete(query);

        ApiResponse<List<StudentResponseDTO>> response = ApiResponse.success(students);

        return ResponseEntity.ok(response);
    }

    /**
     * Update student details
     *
     * PUT /api/students/{id}
     *
     * @param id Student ID
     * @param requestDTO Updated student data
     * @return Updated student details
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentRequestDTO requestDTO) {

        log.info("PUT /api/students/{}", id);

        StudentResponseDTO student = studentService.updateStudent(id, requestDTO);

        ApiResponse<StudentResponseDTO> response = ApiResponse.success(
                "Student updated successfully", student);

        return ResponseEntity.ok(response);
    }

    /**
     * Delete student
     *
     * DELETE /api/students/{id}
     *
     * @param id Student ID
     * @return Success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(@PathVariable Long id) {
        log.info("DELETE /api/students/{}", id);

        studentService.deleteStudent(id);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Student deleted successfully")
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get students with pending fee payments
     *
     * GET /api/students/pending-fees
     *
     * @return List of students with pending fees
     */
    @GetMapping("/pending-fees")
    public ResponseEntity<ApiResponse<List<StudentResponseDTO>>> getStudentsWithPendingFees() {
        log.info("GET /api/students/pending-fees");

        List<StudentResponseDTO> students = studentService.getStudentsWithPendingFees();

        ApiResponse<List<StudentResponseDTO>> response = ApiResponse.success(students);

        return ResponseEntity.ok(response);
    }
}
