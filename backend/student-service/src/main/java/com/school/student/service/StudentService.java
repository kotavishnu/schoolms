package com.school.student.service;

import com.school.student.controller.dto.request.StudentRequest;
import com.school.student.controller.dto.request.StudentUpdateRequest;
import com.school.student.controller.dto.response.StudentResponse;
import com.school.student.domain.exception.BusinessRuleViolationException;
import com.school.student.domain.exception.DuplicateAadhaarException;
import com.school.student.domain.exception.DuplicateMobileException;
import com.school.student.domain.exception.StudentNotFoundException;
import com.school.student.domain.model.Student;
import com.school.student.domain.model.StudentStatus;
import com.school.student.domain.repository.StudentRepository;
import com.school.student.rules.RuleExecutor;
import com.school.student.rules.ValidationResult;
import com.school.student.service.mapper.StudentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Service layer for student management
 * Orchestrates business logic, validation, and persistence
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final RuleExecutor ruleExecutor;

    /**
     * Register new student with validation
     * Transaction: Create
     */
    @Transactional
    public StudentResponse registerStudent(StudentRequest request) {
        log.info("Registering student: {} {}", request.getFirstName(), request.getLastName());

        // Business rule: Check mobile uniqueness
        if (studentRepository.existsByMobile(request.getMobile())) {
            throw new DuplicateMobileException(request.getMobile());
        }

        // Business rule: Check Aadhaar uniqueness (if provided)
        if (request.getAadhaarNumber() != null && !request.getAadhaarNumber().isEmpty()) {
            if (studentRepository.existsByAadhaarNumber(request.getAadhaarNumber())) {
                throw new DuplicateAadhaarException(request.getAadhaarNumber());
            }
        }

        // Map DTO to domain model
        Student student = studentMapper.toDomain(request);

        // Execute Drools validation
        ValidationResult validationResult = ruleExecutor.validate(student);
        if (!validationResult.isValid()) {
            throw new BusinessRuleViolationException(validationResult.getErrors());
        }

        // Generate student ID (format: STD-YYYYMMDD-NNNN)
        student.setStudentId(generateStudentId());
        student.setStatus(StudentStatus.ACTIVE);

        // Persist
        Student savedStudent = studentRepository.save(student);
        log.info("Student registered successfully: {}", savedStudent.getStudentId());

        return studentMapper.toResponse(savedStudent);
    }

    /**
     * Get student by ID with caching
     * Cache: students
     */
    @Cacheable(value = "students", key = "#studentId", unless = "#result == null")
    public StudentResponse getStudentById(String studentId) {
        log.debug("Fetching student: {}", studentId);
        Student student = studentRepository.findByStudentId(studentId)
            .orElseThrow(() -> new StudentNotFoundException(studentId));
        return studentMapper.toResponse(student);
    }

    /**
     * Search students with optional filters
     * Supports pagination and sorting
     */
    public Page<StudentResponse> searchStudents(String lastName, String status, Pageable pageable) {
        log.debug("Searching students with lastName={}, status={}", lastName, status);
        Page<Student> students = studentRepository.searchStudents(lastName, status, pageable);
        return students.map(studentMapper::toResponse);
    }

    /**
     * Update student (only editable fields)
     * Transaction: Update
     * Cache: Evict
     */
    @Transactional
    @CacheEvict(value = "students", key = "#studentId")
    public StudentResponse updateStudent(String studentId, StudentUpdateRequest request) {
        log.info("Updating student: {}", studentId);

        Student existingStudent = studentRepository.findByStudentId(studentId)
            .orElseThrow(() -> new StudentNotFoundException(studentId));

        // Check version for optimistic locking
        if (!existingStudent.getVersion().equals(request.getVersion())) {
            throw new BusinessRuleViolationException(
                java.util.List.of("Student has been modified by another user. Please refresh and try again.")
            );
        }

        // Update only editable fields using domain method
        existingStudent.updateProfile(
            request.getFirstName(),
            request.getLastName(),
            request.getMobile(),
            request.getStatus()
        );

        Student updatedStudent = studentRepository.save(existingStudent);
        log.info("Student updated successfully: {}", studentId);

        return studentMapper.toResponse(updatedStudent);
    }

    /**
     * Delete student
     * Transaction: Delete
     * Cache: Evict
     */
    @Transactional
    @CacheEvict(value = "students", key = "#studentId")
    public void deleteStudent(String studentId) {
        log.info("Deleting student: {}", studentId);
        Student student = studentRepository.findByStudentId(studentId)
            .orElseThrow(() -> new StudentNotFoundException(studentId));

        studentRepository.delete(student);
        log.info("Student deleted successfully: {}", studentId);
    }

    /**
     * Validate phone number availability
     */
    public boolean validatePhone(String mobile) {
        log.debug("Validating phone: {}", mobile);
        return !studentRepository.existsByMobile(mobile);
    }

    /**
     * Generate unique student ID
     * Format: STD-YYYYMMDD-NNNN
     * Example: STD-20260128-0001
     */
    private String generateStudentId() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long sequence = studentRepository.countByCreatedAtDate(LocalDate.now()) + 1;
        return String.format("STD-%s-%04d", datePart, sequence);
    }
}
