package com.school.student.service;

import com.school.student.domain.entity.Student;
import com.school.student.domain.entity.StudentStatus;
import com.school.student.domain.validation.StudentValidationRequest;
import com.school.student.domain.validation.ValidationResult;
import com.school.student.dto.request.StudentRequest;
import com.school.student.dto.request.StudentUpdateRequest;
import com.school.student.dto.response.StudentResponse;
import com.school.student.exception.StudentNotFoundException;
import com.school.student.exception.ValidationException;
import com.school.student.mapper.StudentMapper;
import com.school.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Student Service
 *
 * Core business logic for student management.
 * Implements CRUD operations with Drools validation,
 * caching, and transaction management.
 *
 * Annotations:
 * - @Transactional: All methods run in transactions
 * - @Cacheable/@CacheEvict: Redis caching integration
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class StudentService {

    private final StudentRepository repository;
    private final StudentMapper mapper;
    private final DroolsValidationService validationService;

    // ==================== CREATE ====================

    /**
     * Register a new student
     *
     * Steps:
     * 1. Validate using Drools rules
     * 2. Map DTO to Entity
     * 3. Generate unique student ID
     * 4. Save to database
     * 5. Return response DTO
     *
     * @param request Student registration data
     * @return StudentResponse with generated studentId
     * @throws ValidationException if validation fails
     */
    public StudentResponse registerStudent(StudentRequest request) {
        log.info("Registering student: {} {}", request.getFirstName(), request.getLastName());

        // 1. Validate using Drools
        ValidationResult validationResult = validationService.validateStudent(
            buildValidationRequest(request, null)
        );

        if (!validationResult.isValid()) {
            log.warn("Validation failed: {} errors", validationResult.getErrorCount());
            throw new ValidationException("Student validation failed", validationResult.getErrors());
        }

        // 2. Map DTO to Entity (D-002: MapStruct required)
        Student student = mapper.toEntity(request);

        // 3. Generate Student ID (business logic)
        student.generateStudentId();

        // 4. Save
        Student saved = repository.save(student);
        log.info("Student registered successfully with ID: {}", saved.getStudentId());

        // 5. Return DTO (NEVER return entity directly)
        return mapper.toResponse(saved);
    }

    // ==================== READ ====================

    /**
     * Get student by business key (studentId)
     *
     * Uses Redis cache with key: "students::${studentId}"
     * TTL: 4 hours (configured in CacheConfig)
     *
     * @param studentId Business key (STD-YYYYMMDD-NNNN)
     * @return StudentResponse
     * @throws StudentNotFoundException if not found
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "students", key = "#studentId")
    public StudentResponse getStudentById(String studentId) {
        log.debug("Fetching student: {}", studentId);

        return repository.findByStudentId(studentId)
            .map(mapper::toResponse)
            .orElseThrow(() -> new StudentNotFoundException(studentId));
    }

    // ==================== UPDATE ====================

    /**
     * Update student (editable fields only)
     *
     * BR-STU-007: Only firstName, lastName, mobile, and status can be updated.
     * Immutable fields: dateOfBirth, email, aadhaarNumber, guardianName, etc.
     *
     * Optimistic Locking (D-003): Version field prevents concurrent modification.
     *
     * Cache eviction: Clears "students" and "studentSearchResults" caches.
     *
     * @param studentId Student to update
     * @param request Update data
     * @return Updated StudentResponse
     * @throws StudentNotFoundException if student not found
     * @throws ValidationException if validation fails
     * @throws OptimisticLockException if version conflict (handled by JPA)
     */
    @CacheEvict(value = {"students", "studentSearchResults"}, key = "#studentId")
    public StudentResponse updateStudent(String studentId, StudentUpdateRequest request) {
        log.info("Updating student: {}", studentId);

        // 1. Fetch existing student
        Student student = repository.findByStudentId(studentId)
            .orElseThrow(() -> new StudentNotFoundException(studentId));

        // 2. Validate editable fields
        ValidationResult validationResult = validationService.validateUpdate(
            buildUpdateValidationRequest(request, studentId)
        );

        if (!validationResult.isValid()) {
            log.warn("Update validation failed: {} errors", validationResult.getErrorCount());
            throw new ValidationException("Update validation failed", validationResult.getErrors());
        }

        // 3. Update ONLY allowed fields (BR-STU-007)
        mapper.updateEntityFromRequest(request, student);

        // 4. Set version for optimistic locking
        student.setVersion(request.getVersion());

        // 5. Save (JPA will check version for conflicts)
        Student updated = repository.save(student);
        log.info("Student updated successfully: {}", studentId);

        return mapper.toResponse(updated);
    }

    // ==================== DELETE ====================

    /**
     * Delete student by studentId
     *
     * Cache eviction: Clears all student caches.
     *
     * @param studentId Student to delete
     * @throws StudentNotFoundException if not found
     */
    @CacheEvict(value = {"students", "studentSearchResults"}, allEntries = true)
    public void deleteStudent(String studentId) {
        log.info("Deleting student: {}", studentId);

        Student student = repository.findByStudentId(studentId)
            .orElseThrow(() -> new StudentNotFoundException(studentId));

        repository.delete(student);
        log.info("Student deleted successfully: {}", studentId);
    }

    // ==================== SEARCH ====================

    /**
     * Search students with optional filters
     *
     * Supports:
     * - Search by lastName (partial, case-insensitive)
     * - Filter by status (ACTIVE/INACTIVE)
     * - Pagination
     * - Combined lastName + status filter
     *
     * @param lastName Optional last name filter
     * @param status Optional status filter
     * @param pageable Pagination parameters
     * @return Page of StudentResponse
     */
    @Transactional(readOnly = true)
    public Page<StudentResponse> searchStudents(
        String lastName,
        StudentStatus status,
        Pageable pageable
    ) {
        log.debug("Searching students: lastName={}, status={}, page={}", lastName, status, pageable.getPageNumber());

        Page<Student> students;

        if (lastName != null && status != null) {
            students = repository.findByLastNameContainingIgnoreCaseAndStatus(
                lastName, status, pageable
            );
        } else if (lastName != null) {
            students = repository.findByLastNameContainingIgnoreCase(
                lastName, pageable
            );
        } else if (status != null) {
            students = repository.findByStatus(status, pageable);
        } else {
            students = repository.findAll(pageable);
        }

        log.debug("Found {} students", students.getTotalElements());
        return students.map(mapper::toResponse);
    }

    // ==================== STATISTICS ====================

    /**
     * Get student statistics for dashboard
     *
     * Returns:
     * - total: Total number of students
     * - active: Number of active students
     * - inactive: Number of inactive students
     *
     * @return Map with statistics
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "studentStatistics")
    public Map<String, Long> getStatistics() {
        log.debug("Calculating student statistics");

        long total = repository.count();
        long active = repository.countByStatus(StudentStatus.ACTIVE);
        long inactive = repository.countByStatus(StudentStatus.INACTIVE);

        return Map.of(
            "total", total,
            "active", active,
            "inactive", inactive
        );
    }

    // ==================== HELPER METHODS ====================

    /**
     * Build validation request for create operation
     */
    private StudentValidationRequest buildValidationRequest(StudentRequest request, String excludeStudentId) {
        return StudentValidationRequest.builder()
            .studentId(excludeStudentId)
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .dateOfBirth(request.getDateOfBirth())
            .mobile(request.getMobile())
            .email(request.getEmail())
            .aadhaarNumber(request.getAadhaarNumber())
            .guardianName(request.getGuardianName())
            .motherName(request.getMotherName())
            .address(request.getAddress())
            .identificationMarks(request.getIdentificationMarks())
            .checkMobileUniqueness(true)
            .checkEmailUniqueness(true)
            .build();
    }

    /**
     * Build validation request for update operation
     */
    private StudentValidationRequest buildUpdateValidationRequest(StudentUpdateRequest request, String studentId) {
        return StudentValidationRequest.builder()
            .studentId(studentId)
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .mobile(request.getMobile())
            .checkMobileUniqueness(true)
            .checkEmailUniqueness(false) // Email cannot be changed
            .build();
    }
}
