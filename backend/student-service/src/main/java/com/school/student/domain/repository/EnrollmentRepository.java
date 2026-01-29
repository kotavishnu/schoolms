package com.school.student.domain.repository;

import com.school.student.domain.model.Enrollment;

import java.util.List;

/**
 * Domain repository contract for Enrollment aggregate
 * Implementations in infrastructure layer
 */
public interface EnrollmentRepository {

    /**
     * Save or update an enrollment
     */
    Enrollment save(Enrollment enrollment);

    /**
     * Find all enrollments for a student
     */
    List<Enrollment> findByStudentId(Long studentId);

    /**
     * Check if student already has enrollment for academic year
     * Business rule: One enrollment per student per academic year
     */
    boolean existsByStudentIdAndAcademicYear(Long studentId, String academicYear);
}
