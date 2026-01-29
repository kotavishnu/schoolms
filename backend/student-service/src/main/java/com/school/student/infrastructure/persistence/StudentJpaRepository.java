package com.school.student.infrastructure.persistence;

import com.school.student.domain.model.StudentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Spring Data JPA repository for StudentEntity
 * Provides CRUD operations and custom queries
 */
@Repository
interface StudentJpaRepository extends JpaRepository<StudentEntity, Long> {

    /**
     * Find student by unique student ID
     */
    Optional<StudentEntity> findByStudentId(String studentId);

    /**
     * Check if mobile number already exists
     * Business rule: Mobile must be unique
     */
    boolean existsByMobile(String mobile);

    /**
     * Check if Aadhaar number already exists
     * Business rule: Aadhaar must be unique
     */
    boolean existsByAadhaarNumber(String aadhaarNumber);

    /**
     * Count students created on a specific date
     * Used for student ID sequence generation
     */
    @Query("SELECT COUNT(s) FROM StudentEntity s WHERE DATE(s.createdAt) = :date")
    long countByCreatedAtDate(@Param("date") LocalDate date);

    /**
     * Search students with optional filters
     * Supports pagination and sorting
     */
    @Query("SELECT s FROM StudentEntity s " +
           "WHERE (:lastName IS NULL OR :lastName = '' OR LOWER(s.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) " +
           "AND (:status IS NULL OR s.status = :status)")
    Page<StudentEntity> searchStudents(
        @Param("lastName") String lastName,
        @Param("status") StudentStatus status,
        Pageable pageable
    );
}
