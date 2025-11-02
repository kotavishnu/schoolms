package com.school.management.controller;

import com.school.management.dto.request.ClassRequestDTO;
import com.school.management.dto.response.ApiResponse;
import com.school.management.dto.response.ClassResponseDTO;
import com.school.management.service.ClassService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Class management operations.
 *
 * Provides endpoints for CRUD operations, enrollment tracking, and capacity management.
 *
 * @author School Management Team
 */
@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class ClassController {

    private final ClassService classService;

    /**
     * Create a new class
     *
     * POST /api/classes
     *
     * @param requestDTO Class data
     * @return Created class with HTTP 201
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ClassResponseDTO>> createClass(
            @Valid @RequestBody ClassRequestDTO requestDTO) {

        log.info("POST /api/classes - Creating class: {} - {}",
                requestDTO.getClassNumber(), requestDTO.getSection());

        ClassResponseDTO schoolClass = classService.createClass(requestDTO);

        ApiResponse<ClassResponseDTO> response = ApiResponse.success(
                "Class created successfully", schoolClass);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get class by ID
     *
     * GET /api/classes/{id}
     *
     * @param id Class ID
     * @return Class details
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClassResponseDTO>> getClass(@PathVariable Long id) {
        log.info("GET /api/classes/{}", id);

        ClassResponseDTO schoolClass = classService.getClass(id);

        ApiResponse<ClassResponseDTO> response = ApiResponse.success(schoolClass);

        return ResponseEntity.ok(response);
    }

    /**
     * Get all classes or filter by academic year
     *
     * GET /api/classes
     * GET /api/classes?academicYear=2024-2025
     *
     * @param academicYear Optional academic year filter
     * @return List of classes
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ClassResponseDTO>>> getAllClasses(
            @RequestParam(required = false) String academicYear) {

        log.info("GET /api/classes - Academic year filter: {}", academicYear);

        List<ClassResponseDTO> classes = academicYear != null
                ? classService.getClassesByAcademicYear(academicYear)
                : classService.getAllClasses();

        ApiResponse<List<ClassResponseDTO>> response = ApiResponse.success(classes);

        return ResponseEntity.ok(response);
    }

    /**
     * Get classes by class number (all sections)
     *
     * GET /api/classes/by-number?classNumber=5&academicYear=2024-2025
     *
     * @param classNumber Class number (1-10)
     * @param academicYear Academic year
     * @return List of classes with different sections
     */
    @GetMapping("/by-number")
    public ResponseEntity<ApiResponse<List<ClassResponseDTO>>> getClassesByNumber(
            @RequestParam Integer classNumber,
            @RequestParam String academicYear) {

        log.info("GET /api/classes/by-number?classNumber={}&academicYear={}",
                classNumber, academicYear);

        List<ClassResponseDTO> classes = classService.getClassesByNumber(classNumber, academicYear);

        ApiResponse<List<ClassResponseDTO>> response = ApiResponse.success(classes);

        return ResponseEntity.ok(response);
    }

    /**
     * Get classes with available seats
     *
     * GET /api/classes/available?academicYear=2024-2025
     *
     * @param academicYear Academic year
     * @return List of classes with available capacity
     */
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<ClassResponseDTO>>> getClassesWithAvailableSeats(
            @RequestParam String academicYear) {

        log.info("GET /api/classes/available?academicYear={}", academicYear);

        List<ClassResponseDTO> classes = classService.getClassesWithAvailableSeats(academicYear);

        ApiResponse<List<ClassResponseDTO>> response = ApiResponse.success(classes);

        return ResponseEntity.ok(response);
    }

    /**
     * Get classes that are almost full (>80% capacity)
     *
     * GET /api/classes/almost-full?academicYear=2024-2025
     *
     * @param academicYear Academic year
     * @return List of almost full classes
     */
    @GetMapping("/almost-full")
    public ResponseEntity<ApiResponse<List<ClassResponseDTO>>> getAlmostFullClasses(
            @RequestParam String academicYear) {

        log.info("GET /api/classes/almost-full?academicYear={}", academicYear);

        List<ClassResponseDTO> classes = classService.getAlmostFullClasses(academicYear);

        ApiResponse<List<ClassResponseDTO>> response = ApiResponse.success(classes);

        return ResponseEntity.ok(response);
    }

    /**
     * Get total students count for academic year
     *
     * GET /api/classes/total-students?academicYear=2024-2025
     *
     * @param academicYear Academic year
     * @return Total student count
     */
    @GetMapping("/total-students")
    public ResponseEntity<ApiResponse<Long>> getTotalStudents(
            @RequestParam String academicYear) {

        log.info("GET /api/classes/total-students?academicYear={}", academicYear);

        Long total = classService.getTotalStudents(academicYear);

        ApiResponse<Long> response = ApiResponse.success(total);

        return ResponseEntity.ok(response);
    }

    /**
     * Update class details
     *
     * PUT /api/classes/{id}
     *
     * @param id Class ID
     * @param requestDTO Updated class data
     * @return Updated class details
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ClassResponseDTO>> updateClass(
            @PathVariable Long id,
            @Valid @RequestBody ClassRequestDTO requestDTO) {

        log.info("PUT /api/classes/{}", id);

        ClassResponseDTO schoolClass = classService.updateClass(id, requestDTO);

        ApiResponse<ClassResponseDTO> response = ApiResponse.success(
                "Class updated successfully", schoolClass);

        return ResponseEntity.ok(response);
    }

    /**
     * Delete class
     *
     * DELETE /api/classes/{id}
     *
     * @param id Class ID
     * @return Success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteClass(@PathVariable Long id) {
        log.info("DELETE /api/classes/{}", id);

        classService.deleteClass(id);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Class deleted successfully")
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Check if class exists
     *
     * GET /api/classes/exists?classNumber=5&section=A&academicYear=2024-2025
     *
     * @param classNumber Class number
     * @param section Section
     * @param academicYear Academic year
     * @return Boolean indicating existence
     */
    @GetMapping("/exists")
    public ResponseEntity<ApiResponse<Boolean>> classExists(
            @RequestParam Integer classNumber,
            @RequestParam String section,
            @RequestParam String academicYear) {

        log.info("GET /api/classes/exists?classNumber={}&section={}&academicYear={}",
                classNumber, section, academicYear);

        boolean exists = classService.classExists(classNumber, section, academicYear);

        ApiResponse<Boolean> response = ApiResponse.success(exists);

        return ResponseEntity.ok(response);
    }
}
