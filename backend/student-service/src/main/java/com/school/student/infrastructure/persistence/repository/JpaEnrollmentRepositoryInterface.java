package com.school.student.infrastructure.persistence.repository;

import com.school.student.infrastructure.persistence.entity.EnrollmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository interface for EnrollmentEntity.
 * This interface is automatically implemented by Spring Data JPA.
 *
 * <p>Business Rule Support:
 * <ul>
 *   <li>BR-3: Methods to check/enforce one enrollment per student per academic year</li>
 *   <li>Custom queries for enrollment history and status filtering</li>
 * </ul>
 */
@Repository
public interface JpaEnrollmentRepositoryInterface extends JpaRepository<EnrollmentEntity, Long> {

    /**
     * Find all enrollments for a specific student.
     * Ordered by academic year descending (most recent first).
     *
     * @param studentId the student's internal ID
     * @return List of enrollments for the student
     */
    @Query("SELECT e FROM EnrollmentEntity e WHERE e.student.id = :studentId ORDER BY e.academicYear DESC")
    List<EnrollmentEntity> findByStudentId(@Param("studentId") Long studentId);

    /**
     * Find enrollment for a specific student in a specific academic year.
     * Used to enforce BR-3 (one enrollment per student per year).
     *
     * @param studentId the student's internal ID
     * @param academicYear the academic year (format: "YYYY-YYYY")
     * @return Optional containing the enrollment if found
     */
    @Query("SELECT e FROM EnrollmentEntity e WHERE e.student.id = :studentId AND e.academicYear = :academicYear")
    Optional<EnrollmentEntity> findByStudentIdAndAcademicYear(
            @Param("studentId") Long studentId,
            @Param("academicYear") String academicYear
    );

    /**
     * Check if enrollment exists for student in academic year.
     * Used for BR-3 validation.
     *
     * @param studentId the student's internal ID
     * @param academicYear the academic year
     * @return true if enrollment exists, false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM EnrollmentEntity e WHERE e.student.id = :studentId AND e.academicYear = :academicYear")
    boolean existsByStudentIdAndAcademicYear(
            @Param("studentId") Long studentId,
            @Param("academicYear") String academicYear
    );

    /**
     * Find ACTIVE enrollments for a specific student.
     *
     * @param studentId the student's internal ID
     * @return List of active enrollments
     */
    @Query("SELECT e FROM EnrollmentEntity e WHERE e.student.id = :studentId AND e.status = 'ACTIVE'")
    List<EnrollmentEntity> findActiveEnrollmentsByStudentId(@Param("studentId") Long studentId);

    /**
     * Find all enrollments for a specific academic year.
     *
     * @param academicYear the academic year
     * @return List of enrollments for the academic year
     */
    List<EnrollmentEntity> findByAcademicYear(String academicYear);

    /**
     * Find enrollments by academic year and status.
     *
     * @param academicYear the academic year
     * @param status the enrollment status
     * @return List of enrollments matching the criteria
     */
    @Query("SELECT e FROM EnrollmentEntity e WHERE e.academicYear = :academicYear AND e.status = :status")
    List<EnrollmentEntity> findByAcademicYearAndStatus(
            @Param("academicYear") String academicYear,
            @Param("status") EnrollmentEntity.EnrollmentStatusEnum status
    );

    /**
     * Count enrollments for a specific student.
     *
     * @param studentId the student's internal ID
     * @return count of enrollments
     */
    @Query("SELECT COUNT(e) FROM EnrollmentEntity e WHERE e.student.id = :studentId")
    long countByStudentId(@Param("studentId") Long studentId);
}
