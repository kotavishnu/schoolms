package com.school.student.domain.repository;

import com.school.student.domain.model.Enrollment;
import com.school.student.domain.model.EnrollmentStatus;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Enrollment entity.
 * Defines contracts for enrollment persistence operations.
 *
 * <p>Following Domain-Driven Design principles:
 * <ul>
 *   <li>Uses domain models (Enrollment) NOT infrastructure entities</li>
 *   <li>Supports business queries (by student, academic year, status)</li>
 *   <li>Enforces BR-3: One enrollment per student per academic year</li>
 * </ul>
 */
public interface EnrollmentRepository {

    /**
     * Persists a new enrollment or updates an existing enrollment.
     *
     * @param enrollment the enrollment to save
     * @return the saved enrollment with generated ID
     * @throws IllegalArgumentException if enrollment is null
     */
    Enrollment save(Enrollment enrollment);

    /**
     * Finds an enrollment by its internal ID.
     *
     * @param id the internal primary key
     * @return Optional containing the enrollment if found, empty otherwise
     * @throws IllegalArgumentException if id is null
     */
    Optional<Enrollment> findById(Long id);

    /**
     * Retrieves all enrollments for a specific student.
     * Ordered by academic year descending (most recent first).
     *
     * @param studentId the student's internal ID
     * @return List of enrollments for the student (empty list if none found)
     * @throws IllegalArgumentException if studentId is null
     */
    List<Enrollment> findByStudentId(Long studentId);

    /**
     * Retrieves all enrollments for a specific student in a specific academic year.
     * Used to enforce BR-3 (one enrollment per student per year).
     *
     * @param studentId the student's internal ID
     * @param academicYear the academic year (format: "YYYY-YYYY")
     * @return Optional containing the enrollment if found, empty otherwise
     * @throws IllegalArgumentException if studentId or academicYear is null
     */
    Optional<Enrollment> findByStudentIdAndAcademicYear(Long studentId, String academicYear);

    /**
     * Checks if an enrollment exists for a student in a specific academic year.
     * Used for BR-3 validation during enrollment creation.
     *
     * @param studentId the student's internal ID
     * @param academicYear the academic year
     * @return true if enrollment exists, false otherwise
     * @throws IllegalArgumentException if studentId or academicYear is null
     */
    boolean existsByStudentIdAndAcademicYear(Long studentId, String academicYear);

    /**
     * Retrieves all ACTIVE enrollments for a specific student.
     *
     * @param studentId the student's internal ID
     * @return List of active enrollments (empty list if none found)
     * @throws IllegalArgumentException if studentId is null
     */
    List<Enrollment> findActiveEnrollmentsByStudentId(Long studentId);

    /**
     * Retrieves all enrollments for a specific academic year.
     *
     * @param academicYear the academic year (format: "YYYY-YYYY")
     * @return List of enrollments for the academic year
     * @throws IllegalArgumentException if academicYear is null
     */
    List<Enrollment> findByAcademicYear(String academicYear);

    /**
     * Retrieves enrollments by status for a specific academic year.
     *
     * @param academicYear the academic year
     * @param status the enrollment status
     * @return List of enrollments matching the criteria
     * @throws IllegalArgumentException if any parameter is null
     */
    List<Enrollment> findByAcademicYearAndStatus(String academicYear, EnrollmentStatus status);

    /**
     * Deletes an enrollment by its internal ID.
     *
     * @param id the internal primary key
     * @throws IllegalArgumentException if id is null
     */
    void deleteById(Long id);

    /**
     * Counts total enrollments for a student.
     *
     * @param studentId the student's internal ID
     * @return count of enrollments
     * @throws IllegalArgumentException if studentId is null
     */
    long countByStudentId(Long studentId);
}
