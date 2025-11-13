package com.school.management.infrastructure.persistence;

import com.school.management.domain.student.Student;
import com.school.management.domain.student.StudentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Student entity
 * Provides database access methods for student management
 *
 * @author School Management System
 * @version 1.0
 */
@Repository
public interface StudentRepository extends BaseRepository<Student, Long> {

    /**
     * Finds a student by their unique student code
     *
     * @param studentCode the student code (e.g., STU-2025-00001)
     * @return Optional containing the student if found
     */
    Optional<Student> findByStudentCode(String studentCode);

    /**
     * Finds students by status and admission date range
     *
     * @param status     the student status to filter by
     * @param startDate  the start date of admission range
     * @param endDate    the end date of admission range
     * @return list of students matching the criteria
     */
    @Query("SELECT s FROM Student s WHERE s.status = :status " +
        "AND s.admissionDate BETWEEN :startDate AND :endDate")
    List<Student> findByStatusAndAdmissionDateBetween(
        @Param("status") StudentStatus status,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Finds students with any of the specified statuses
     *
     * @param statuses list of statuses to filter by
     * @return list of students with matching statuses
     */
    List<Student> findByStatusIn(List<StudentStatus> statuses);

    /**
     * Finds students by status with pagination
     *
     * @param status   the status to filter by
     * @param pageable pagination information
     * @return page of students
     */
    Page<Student> findByStatus(StudentStatus status, Pageable pageable);

    /**
     * Finds a student by mobile hash and status
     * Used for checking mobile number uniqueness for active students
     *
     * @param mobileHash the SHA-256 hash of the mobile number
     * @param status     the student status
     * @return Optional containing the student if found
     */
    Optional<Student> findByMobileHashAndStatus(String mobileHash, StudentStatus status);

    /**
     * Counts students by status
     *
     * @param status the status to count
     * @return count of students with the specified status
     */
    long countByStatus(StudentStatus status);

    /**
     * Checks if a mobile hash exists in the database
     * Used for mobile number uniqueness validation
     *
     * @param mobileHash the SHA-256 hash of the mobile number
     * @return true if the mobile hash exists
     */
    boolean existsByMobileHash(String mobileHash);

    /**
     * Finds all active students
     *
     * @return list of active students
     */
    @Query("SELECT s FROM Student s WHERE s.status = 'ACTIVE'")
    List<Student> findAllActive();

    /**
     * Finds students by status with custom query and pagination
     * Includes fetch join for guardians to avoid N+1 queries
     *
     * @param status   the status to filter by
     * @param pageable pagination information
     * @return page of students with guardians fetched
     */
    @Query("SELECT DISTINCT s FROM Student s " +
        "LEFT JOIN FETCH s.guardians " +
        "WHERE s.status = :status")
    Page<Student> findByStatusWithGuardians(@Param("status") StudentStatus status, Pageable pageable);
}
