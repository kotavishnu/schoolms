package com.sms.student.infrastructure.persistence.repository;

import com.sms.student.domain.model.StudentStatus;
import com.sms.student.infrastructure.persistence.entity.StudentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for Student entity.
 * Provides database operations for student management.
 */
@Repository
public interface StudentRepository extends JpaRepository<StudentEntity, Long> {

    /**
     * Find student by business key (student_key).
     *
     * @param studentKey the student business key
     * @return Optional containing the student if found
     */
    Optional<StudentEntity> findByStudentKey(String studentKey);

    /**
     * Check if a student with the given mobile number exists.
     *
     * @param mobile the mobile number
     * @return true if exists, false otherwise
     */
    boolean existsByMobile(String mobile);

    /**
     * Check if a student with the given email exists.
     *
     * @param email the email address
     * @return true if exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Check if a student with the given Adhaar number exists.
     *
     * @param adhaarNumber the Adhaar number
     * @return true if exists, false otherwise
     */
    boolean existsByAdhaarNumber(String adhaarNumber);

    /**
     * Find the maximum sequence number for student key generation.
     * Student keys follow the format: STU-YYYY-NNNN
     *
     * @param yearPrefix the year prefix (e.g., "STU-2025-")
     * @return the maximum sequence number, or null if none exists
     */
    @Query("SELECT MAX(CAST(SUBSTRING(s.studentKey, LENGTH(:yearPrefix) + 1) AS int)) " +
           "FROM StudentEntity s " +
           "WHERE s.studentKey LIKE CONCAT(:yearPrefix, '%')")
    Integer findMaxSequenceForYear(@Param("yearPrefix") String yearPrefix);

    /**
     * Search students by last name (case-insensitive partial match).
     *
     * @param lastName the last name to search
     * @param pageable pagination information
     * @return page of students matching the last name
     */
    Page<StudentEntity> findByLastNameContainingIgnoreCase(String lastName, Pageable pageable);

    /**
     * Search students by guardian name (case-insensitive partial match).
     *
     * @param guardianName the guardian name to search
     * @param pageable pagination information
     * @return page of students matching the guardian name
     */
    @Query("SELECT s FROM StudentEntity s WHERE LOWER(s.fatherNameOrGuardian) LIKE LOWER(CONCAT('%', :guardianName, '%'))")
    Page<StudentEntity> findByFatherNameOrGuardianContainingIgnoreCase(@Param("guardianName") String guardianName, Pageable pageable);

    /**
     * Search students by status.
     *
     * @param status the enrollment status
     * @param pageable pagination information
     * @return page of students with the given status
     */
    Page<StudentEntity> findByStatus(StudentStatus status, Pageable pageable);

    /**
     * Search students by last name and status.
     *
     * @param lastName the last name to search
     * @param status the enrollment status
     * @param pageable pagination information
     * @return page of students matching both criteria
     */
    Page<StudentEntity> findByLastNameContainingIgnoreCaseAndStatus(
            String lastName,
            StudentStatus status,
            Pageable pageable
    );

    /**
     * Search students by guardian name and status.
     *
     * @param guardianName the guardian name to search
     * @param status the enrollment status
     * @param pageable pagination information
     * @return page of students matching both criteria
     */
    @Query("SELECT s FROM StudentEntity s WHERE LOWER(s.fatherNameOrGuardian) LIKE LOWER(CONCAT('%', :guardianName, '%')) AND s.status = :status")
    Page<StudentEntity> findByFatherNameOrGuardianContainingIgnoreCaseAndStatus(
            @Param("guardianName") String guardianName,
            @Param("status") StudentStatus status,
            Pageable pageable
    );

    /**
     * Search students by last name OR guardian name (case-insensitive partial match).
     *
     * @param lastName the last name to search
     * @param guardianName the guardian name to search
     * @param pageable pagination information
     * @return page of students matching either criteria
     */
    @Query("SELECT s FROM StudentEntity s WHERE " +
           "LOWER(s.lastName) LIKE LOWER(CONCAT('%', :lastName, '%')) OR " +
           "LOWER(s.fatherNameOrGuardian) LIKE LOWER(CONCAT('%', :guardianName, '%'))")
    Page<StudentEntity> searchByLastNameOrGuardian(
            @Param("lastName") String lastName,
            @Param("guardianName") String guardianName,
            Pageable pageable
    );
}
