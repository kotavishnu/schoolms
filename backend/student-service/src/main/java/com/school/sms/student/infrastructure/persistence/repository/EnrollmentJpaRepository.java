package com.school.sms.student.infrastructure.persistence.repository;

import com.school.sms.student.infrastructure.persistence.entity.EnrollmentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for Enrollment persistence.
 *
 * <p>Provides CRUD operations and custom queries for EnrollmentJpaEntity.</p>
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
@Repository
public interface EnrollmentJpaRepository extends JpaRepository<EnrollmentJpaEntity, Long> {

    /**
     * Finds all enrollments for a specific student.
     *
     * @param studentId the student's database ID
     * @return a list of enrollment entities
     */
    List<EnrollmentJpaEntity> findByStudentIdOrderByAcademicYearDesc(Long studentId);

    /**
     * Finds an enrollment for a student in a specific academic year.
     *
     * @param studentId the student's database ID
     * @param academicYear the academic year
     * @return an Optional containing the enrollment entity if found
     */
    Optional<EnrollmentJpaEntity> findByStudentIdAndAcademicYear(Long studentId, String academicYear);

    /**
     * Finds all enrollments for a specific academic year.
     *
     * @param academicYear the academic year
     * @return a list of enrollment entities
     */
    List<EnrollmentJpaEntity> findByAcademicYear(String academicYear);

    /**
     * Checks if an enrollment exists for a student in a specific academic year.
     *
     * @param studentId the student's database ID
     * @param academicYear the academic year
     * @return true if exists, false otherwise
     */
    boolean existsByStudentIdAndAcademicYear(Long studentId, String academicYear);
}
