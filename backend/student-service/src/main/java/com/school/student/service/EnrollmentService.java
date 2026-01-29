package com.school.student.service;

import com.school.student.controller.dto.request.EnrollmentRequest;
import com.school.student.controller.dto.response.EnrollmentResponse;
import com.school.student.domain.exception.EnrollmentConflictException;
import com.school.student.domain.exception.StudentNotFoundException;
import com.school.student.domain.model.Enrollment;
import com.school.student.domain.model.Student;
import com.school.student.domain.repository.EnrollmentRepository;
import com.school.student.domain.repository.StudentRepository;
import com.school.student.service.mapper.EnrollmentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for enrollment management
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentMapper enrollmentMapper;

    /**
     * Create new enrollment for student
     * Business rule: One enrollment per student per academic year
     */
    @Transactional
    public EnrollmentResponse createEnrollment(String studentId, EnrollmentRequest request) {
        log.info("Creating enrollment for student: {} in year {}", studentId, request.getAcademicYear());

        // Verify student exists
        Student student = studentRepository.findByStudentId(studentId)
            .orElseThrow(() -> new StudentNotFoundException(studentId));

        // Business rule: Check for duplicate enrollment in same academic year
        if (enrollmentRepository.existsByStudentIdAndAcademicYear(student.getId(), request.getAcademicYear())) {
            throw new EnrollmentConflictException(student.getId(), request.getAcademicYear());
        }

        // Map DTO to domain model
        Enrollment enrollment = enrollmentMapper.toDomain(request);
        enrollment.setStudentId(student.getId());

        // Persist
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        log.info("Enrollment created successfully for student: {}", studentId);

        return enrollmentMapper.toResponse(savedEnrollment);
    }

    /**
     * Get enrollment history for student
     * Returns all enrollments ordered by date (latest first)
     */
    public List<EnrollmentResponse> getEnrollmentHistory(String studentId) {
        log.debug("Fetching enrollment history for student: {}", studentId);

        // Verify student exists
        Student student = studentRepository.findByStudentId(studentId)
            .orElseThrow(() -> new StudentNotFoundException(studentId));

        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(student.getId());

        return enrollments.stream()
            .map(enrollmentMapper::toResponse)
            .collect(Collectors.toList());
    }
}
