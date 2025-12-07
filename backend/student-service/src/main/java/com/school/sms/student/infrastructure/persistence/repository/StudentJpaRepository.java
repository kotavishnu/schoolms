package com.school.sms.student.infrastructure.persistence.repository;

import com.school.sms.student.infrastructure.persistence.entity.StudentJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for Student persistence.
 *
 * <p>Provides CRUD operations and custom queries for StudentJpaEntity.</p>
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
@Repository
public interface StudentJpaRepository extends JpaRepository<StudentJpaEntity, Long> {

    /**
     * Finds a student by their business key (student ID).
     *
     * @param studentId the student ID
     * @return an Optional containing the student entity if found
     */
    Optional<StudentJpaEntity> findByStudentId(String studentId);

    /**
     * Finds a student by mobile number.
     *
     * @param mobile the mobile number
     * @return an Optional containing the student entity if found
     */
    Optional<StudentJpaEntity> findByMobile(String mobile);

    /**
     * Finds students by last name (case-insensitive, partial match).
     *
     * @param lastName the last name to search for
     * @param pageable pagination information
     * @return a page of student entities
     */
    Page<StudentJpaEntity> findByLastNameContainingIgnoreCase(String lastName, Pageable pageable);

    /**
     * Finds students by father's name (case-insensitive, partial match).
     *
     * @param fathersName the father's name to search for
     * @param pageable pagination information
     * @return a page of student entities
     */
    Page<StudentJpaEntity> findByFathersNameContainingIgnoreCase(String fathersName, Pageable pageable);

    /**
     * Finds students by status.
     *
     * @param status the student status
     * @param pageable pagination information
     * @return a page of student entities
     */
    Page<StudentJpaEntity> findByStatus(StudentJpaEntity.StudentStatusEnum status, Pageable pageable);

    /**
     * Checks if a student exists with the given mobile number.
     *
     * @param mobile the mobile number
     * @return true if exists, false otherwise
     */
    boolean existsByMobile(String mobile);

    /**
     * Checks if a student exists with the given Aadhaar number.
     *
     * @param aadhaarNumber the Aadhaar number
     * @return true if exists, false otherwise
     */
    boolean existsByAadhaarNumber(String aadhaarNumber);

    /**
     * Counts the total number of active students.
     *
     * @return the count of active students
     */
    @Query("SELECT COUNT(s) FROM StudentJpaEntity s WHERE s.status = 'ACTIVE'")
    long countActiveStudents();

    /**
     * Checks if a student exists with the given mobile number excluding a specific student ID.
     * Useful for update operations to allow keeping the same mobile.
     *
     * @param mobile the mobile number
     * @param studentId the student ID to exclude
     * @return true if exists, false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM StudentJpaEntity s " +
           "WHERE s.mobile = :mobile AND s.studentId <> :studentId")
    boolean existsByMobileAndStudentIdNot(@Param("mobile") String mobile, @Param("studentId") String studentId);
}
