package com.school.student.repository;

import com.school.student.domain.entity.Enrollment;
import com.school.student.domain.entity.EnrollmentStatus;
import com.school.student.domain.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Enrollment Repository
 *
 * Data access layer for Enrollment entity.
 * Provides query methods for enrollment history tracking.
 */
@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    /**
     * Find all enrollments for a specific student
     * Ordered by enrollment date descending (most recent first)
     */
    @Query("SELECT e FROM Enrollment e WHERE e.student.studentId = :studentId ORDER BY e.enrollmentDate DESC")
    List<Enrollment> findByStudentId(@Param("studentId") String studentId);

    /**
     * Find enrollments by student and status
     */
    List<Enrollment> findByStudentAndStatus(Student student, EnrollmentStatus status);

    /**
     * Find enrollments for a specific academic year
     */
    List<Enrollment> findByAcademicYear(String academicYear);

    /**
     * Find active enrollment for a student
     */
    @Query("SELECT e FROM Enrollment e WHERE e.student.studentId = :studentId AND e.status = 'ACTIVE'")
    List<Enrollment> findActiveEnrollmentsByStudentId(@Param("studentId") String studentId);
}
