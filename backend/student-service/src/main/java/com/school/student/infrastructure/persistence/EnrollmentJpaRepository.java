package com.school.student.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for EnrollmentEntity
 */
@Repository
interface EnrollmentJpaRepository extends JpaRepository<EnrollmentEntity, Long> {

    /**
     * Find all enrollments for a student
     * Orders by enrollment date descending (latest first)
     */
    @Query("SELECT e FROM EnrollmentEntity e WHERE e.student.id = :studentId ORDER BY e.enrollmentDate DESC")
    List<EnrollmentEntity> findByStudentId(@Param("studentId") Long studentId);

    /**
     * Check if student already has enrollment for academic year
     * Business rule: One enrollment per student per academic year
     */
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END " +
           "FROM EnrollmentEntity e WHERE e.student.id = :studentId AND e.academicYear = :academicYear")
    boolean existsByStudentIdAndAcademicYear(
        @Param("studentId") Long studentId,
        @Param("academicYear") String academicYear
    );
}
