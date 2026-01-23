package com.school.student.repository;

import com.school.student.domain.entity.Student;
import com.school.student.domain.entity.StudentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Student Repository
 *
 * Data access layer for Student entity.
 * Provides query methods for student CRUD operations,
 * uniqueness checks, and search functionality.
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // ==================== Business Key Lookup ====================

    /**
     * Find student by business key (studentId)
     * Preferred lookup method for client requests
     */
    Optional<Student> findByStudentId(String studentId);

    // ==================== Uniqueness Checks ====================

    /**
     * Check if mobile number exists
     * Used for BR-STU-002: Mobile uniqueness validation
     */
    boolean existsByMobile(String mobile);

    /**
     * Check if mobile exists for a different student
     * Used during update operations to validate uniqueness
     * excluding the current student
     */
    boolean existsByMobileAndStudentIdNot(String mobile, String studentId);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Check if email exists for a different student
     * Used during update operations
     */
    boolean existsByEmailAndStudentIdNot(String email, String studentId);

    /**
     * Check if Aadhaar number exists
     */
    boolean existsByAadhaarNumber(String aadhaarNumber);

    // ==================== Search Queries ====================

    /**
     * Search students by last name (case-insensitive, partial match)
     * Supports pagination
     */
    Page<Student> findByLastNameContainingIgnoreCase(
        String lastName,
        Pageable pageable
    );

    /**
     * Find students by status
     * Supports pagination
     */
    Page<Student> findByStatus(
        StudentStatus status,
        Pageable pageable
    );

    /**
     * Search students by last name AND status
     * Combined filter with pagination
     */
    Page<Student> findByLastNameContainingIgnoreCaseAndStatus(
        String lastName,
        StudentStatus status,
        Pageable pageable
    );

    /**
     * Search students by guardian name (partial match)
     * Custom query to avoid N+1 problem
     */
    @Query("SELECT s FROM Student s WHERE s.guardianName LIKE %:guardianName%")
    Page<Student> findByGuardianNameContaining(
        @Param("guardianName") String guardianName,
        Pageable pageable
    );

    // ==================== Count Queries ====================

    /**
     * Count students by status
     * Used for dashboard statistics
     */
    long countByStatus(StudentStatus status);
}
