package com.school.student.service;

import com.school.student.domain.entity.Enrollment;
import com.school.student.domain.entity.Student;
import com.school.student.dto.request.EnrollmentRequest;
import com.school.student.dto.response.EnrollmentResponse;
import com.school.student.exception.StudentNotFoundException;
import com.school.student.mapper.EnrollmentMapper;
import com.school.student.repository.EnrollmentRepository;
import com.school.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Enrollment Service
 *
 * Manages student enrollment history.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentMapper mapper;

    /**
     * Create new enrollment for a student
     */
    public EnrollmentResponse createEnrollment(String studentId, EnrollmentRequest request) {
        log.info("Creating enrollment for student: {}", studentId);

        Student student = studentRepository.findByStudentId(studentId)
            .orElseThrow(() -> new StudentNotFoundException(studentId));

        Enrollment enrollment = mapper.toEntity(request);
        enrollment.setStudent(student);

        Enrollment saved = enrollmentRepository.save(enrollment);
        log.info("Enrollment created successfully for student: {}", studentId);

        return mapper.toResponse(saved);
    }

    /**
     * Get enrollment history for a student
     */
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getEnrollmentHistory(String studentId) {
        log.debug("Fetching enrollment history for student: {}", studentId);

        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);

        return enrollments.stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());
    }
}
