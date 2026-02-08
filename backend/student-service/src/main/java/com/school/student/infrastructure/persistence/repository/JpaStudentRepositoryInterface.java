package com.school.student.infrastructure.persistence.repository;

import com.school.student.infrastructure.persistence.entity.StudentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository interface for StudentEntity.
 * This interface is automatically implemented by Spring Data JPA.
 *
 * <p>Performance Optimizations:
 * <ul>
 *   <li>@EntityGraph used to prevent N+1 queries when loading enrollments</li>
 *   <li>Custom queries for complex search operations</li>
 *   <li>Indexed columns used in WHERE clauses</li>
 * </ul>
 *
 * <p>Note: This is infrastructure layer. Domain layer uses {@code StudentRepository}.
 */
@Repository
public interface JpaStudentRepositoryInterface extends JpaRepository<StudentEntity, Long> {

    /**
     * Find student by business key (studentId) with eager loading of enrollments.
     * Uses @EntityGraph to prevent N+1 query problem.
     *
     * @param studentId the unique business identifier
     * @return Optional containing StudentEntity if found
     */
    @EntityGraph(attributePaths = {"enrollments"})
    @Query("SELECT s FROM StudentEntity s WHERE s.studentId = :studentId")
    Optional<StudentEntity> findByStudentIdWithEnrollments(@Param("studentId") String studentId);

    /**
     * Find student by business key (studentId) without enrollments.
     * Lighter query when enrollments not needed.
     *
     * @param studentId the unique business identifier
     * @return Optional containing StudentEntity if found
     */
    Optional<StudentEntity> findByStudentId(String studentId);

    /**
     * Search students by last name with case-insensitive partial match.
     * Supports pagination.
     *
     * @param lastName the last name to search
     * @param pageable pagination parameters
     * @return Page of students matching the criteria
     */
    @Query("SELECT s FROM StudentEntity s WHERE LOWER(s.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))")
    Page<StudentEntity> findByLastNameContaining(@Param("lastName") String lastName, Pageable pageable);

    /**
     * Find students by status with pagination.
     *
     * @param status the student status
     * @param pageable pagination parameters
     * @return Page of students with the specified status
     */
    Page<StudentEntity> findByStatus(StudentEntity.StudentStatusEnum status, Pageable pageable);

    /**
     * Find students by last name and status with pagination.
     *
     * @param lastName the last name to search
     * @param status the student status
     * @param pageable pagination parameters
     * @return Page of students matching both criteria
     */
    @Query("SELECT s FROM StudentEntity s WHERE LOWER(s.lastName) LIKE LOWER(CONCAT('%', :lastName, '%')) AND s.status = :status")
    Page<StudentEntity> findByLastNameContainingAndStatus(
            @Param("lastName") String lastName,
            @Param("status") StudentEntity.StudentStatusEnum status,
            Pageable pageable
    );

    /**
     * Check if mobile number exists.
     * Used for BR-2 (mobile uniqueness) validation during registration.
     *
     * @param mobile the mobile number to check
     * @return true if mobile exists, false otherwise
     */
    boolean existsByMobile(String mobile);

    /**
     * Check if mobile number exists for any student except the specified one.
     * Used for BR-2 validation during profile updates.
     *
     * @param mobile the mobile number to check
     * @param id the student ID to exclude
     * @return true if mobile exists for another student
     */
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM StudentEntity s WHERE s.mobile = :mobile AND s.id <> :id")
    boolean existsByMobileAndIdNot(@Param("mobile") String mobile, @Param("id") Long id);

    /**
     * Count students by status.
     *
     * @param status the student status
     * @return count of students with the specified status
     */
    long countByStatus(StudentEntity.StudentStatusEnum status);
}
