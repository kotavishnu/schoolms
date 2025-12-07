package com.school.sms.student.domain.repository;

import com.school.sms.student.domain.model.Enrollment;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Enrollment domain entity.
 *
 * <p>This is a domain-level contract. The infrastructure layer
 * will provide the concrete implementation using JPA.</p>
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
public interface EnrollmentRepository {

    /**
     * Saves an enrollment record (create or update).
     *
     * @param enrollment the enrollment to save
     * @return the saved enrollment with assigned ID and version
     */
    Enrollment save(Enrollment enrollment);

    /**
     * Finds all enrollments for a specific student.
     *
     * @param studentId the student's database ID
     * @return a list of enrollments
     */
    List<Enrollment> findByStudentId(Long studentId);

    /**
     * Finds an enrollment for a student in a specific academic year.
     *
     * @param studentId the student's database ID
     * @param academicYear the academic year
     * @return an Optional containing the enrollment if found
     */
    Optional<Enrollment> findByStudentIdAndAcademicYear(Long studentId, String academicYear);

    /**
     * Finds all enrollments for a specific academic year.
     *
     * @param academicYear the academic year
     * @return a list of enrollments
     */
    List<Enrollment> findByAcademicYear(String academicYear);

    /**
     * Checks if an enrollment exists for a student in a specific academic year.
     *
     * @param studentId the student's database ID
     * @param academicYear the academic year
     * @return true if exists, false otherwise
     */
    boolean existsByStudentIdAndAcademicYear(Long studentId, String academicYear);
}
