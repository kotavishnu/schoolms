package com.school.management.infrastructure.persistence;

import com.school.management.domain.academic.AcademicYear;
import com.school.management.domain.academic.SchoolClass;
import com.school.management.domain.student.Enrollment;
import com.school.management.domain.student.EnrollmentStatus;
import com.school.management.domain.student.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Enrollment entity
 *
 * @author School Management System
 * @version 1.0
 */
@Repository
public interface EnrollmentRepository extends BaseRepository<Enrollment, Long> {

    /**
     * Find all enrollments for a student
     */
    List<Enrollment> findByStudent(Student student);

    /**
     * Find enrollments by student ID
     */
    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId")
    List<Enrollment> findByStudentId(@Param("studentId") Long studentId);

    /**
     * Find enrollments for a class
     */
    List<Enrollment> findByEnrolledClass(SchoolClass schoolClass);

    /**
     * Find active enrollment for student in a class
     */
    Optional<Enrollment> findByStudentAndEnrolledClass(Student student, SchoolClass schoolClass);

    /**
     * Find enrollments by status
     */
    List<Enrollment> findByStatus(EnrollmentStatus status);

    /**
     * Find active enrollments for a student
     */
    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.status = 'ENROLLED'")
    List<Enrollment> findActiveEnrollmentsByStudentId(@Param("studentId") Long studentId);

    /**
     * Count enrollments for a class
     */
    long countByEnrolledClass(SchoolClass schoolClass);

    /**
     * Find enrollment by student and academic year
     */
    @Query("SELECT e FROM Enrollment e WHERE e.student = :student AND " +
        "e.enrolledClass.academicYear = :academicYear")
    Optional<Enrollment> findByStudentAndAcademicYear(
        @Param("student") Student student,
        @Param("academicYear") AcademicYear academicYear);

    /**
     * Find enrollments by date range
     */
    @Query("SELECT e FROM Enrollment e WHERE e.enrollmentDate BETWEEN :startDate AND :endDate")
    Page<Enrollment> findByEnrollmentDateBetween(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        Pageable pageable);
}
