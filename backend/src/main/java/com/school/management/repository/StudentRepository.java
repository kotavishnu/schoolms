package com.school.management.repository;

import com.school.management.model.Student;
import com.school.management.model.StudentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Student entity.
 *
 * Provides CRUD operations and custom queries for student management.
 *
 * @author School Management Team
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    /**
     * Find students by class ID
     * @param classId Class identifier
     * @return List of students in the class
     */
    List<Student> findBySchoolClassId(Long classId);

    /**
     * Find student by mobile number
     * @param mobile Mobile number (must be unique)
     * @return Optional student
     */
    Optional<Student> findByMobile(String mobile);

    /**
     * Find students by status
     * @param status Student status (ACTIVE, INACTIVE, etc.)
     * @return List of students with given status
     */
    List<Student> findByStatus(StudentStatus status);

    /**
     * Find students by class ID and status
     * @param classId Class identifier
     * @param status Student status
     * @return List of students
     */
    List<Student> findBySchoolClassIdAndStatus(Long classId, StudentStatus status);

    /**
     * Search students by name (first or last name) - Case insensitive
     * @param query Search query string
     * @return List of matching students
     */
    @Query("SELECT s FROM Student s WHERE " +
           "LOWER(s.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.lastName) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Student> searchByName(@Param("query") String query);

    /**
     * Find active students by class - Optimized with JPQL
     * @param classId Class identifier
     * @return List of active students ordered by name
     */
    @Query("SELECT s FROM Student s WHERE s.schoolClass.id = :classId AND s.status = 'ACTIVE' " +
           "ORDER BY s.firstName, s.lastName")
    List<Student> findActiveStudentsByClass(@Param("classId") Long classId);

    /**
     * Check if mobile number exists
     * @param mobile Mobile number to check
     * @return true if mobile exists
     */
    boolean existsByMobile(String mobile);

    /**
     * Count students in a class
     * @param classId Class identifier
     * @return Number of students
     */
    long countBySchoolClassId(Long classId);

    /**
     * Count active students in a class
     * @param classId Class identifier
     * @param status Student status
     * @return Number of active students
     */
    long countBySchoolClassIdAndStatus(Long classId, StudentStatus status);

    /**
     * Find students with pending fee payments
     * @return List of students with pending fees
     */
    @Query("SELECT DISTINCT s FROM Student s " +
           "JOIN s.feeJournals fj " +
           "WHERE fj.status IN ('PENDING', 'OVERDUE') AND s.status = 'ACTIVE'")
    List<Student> findStudentsWithPendingFees();

    /**
     * Search students by name or mobile for autocomplete
     * @param query Search query
     * @return List of matching students (limited to 10 results)
     */
    @Query("SELECT s FROM Student s WHERE " +
           "LOWER(s.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "s.mobile LIKE CONCAT('%', :query, '%') " +
           "ORDER BY s.firstName, s.lastName")
    List<Student> searchForAutocomplete(@Param("query") String query);
}
