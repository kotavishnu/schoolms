package com.school.student.domain.repository;

import com.school.student.domain.model.Student;
import com.school.student.domain.model.StudentStatus;
import com.school.student.domain.model.valueobject.Mobile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Repository interface for Student aggregate root.
 * Defines contracts for persistence operations in domain layer.
 * Implementation will be provided by infrastructure layer (adapter pattern).
 *
 * <p>Following Domain-Driven Design principles:
 * <ul>
 *   <li>Uses domain models (Student) NOT infrastructure entities (StudentEntity)</li>
 *   <li>Returns Optional for single results to handle not-found scenarios</li>
 *   <li>Uses Spring Data Pageable for pagination support</li>
 *   <li>All methods operate on domain value objects (Mobile, StudentStatus)</li>
 * </ul>
 */
public interface StudentRepository {

    /**
     * Persists a new student or updates an existing student.
     *
     * @param student the student domain model to save
     * @return the saved student with generated ID and version
     * @throws IllegalArgumentException if student is null
     */
    Student save(Student student);

    /**
     * Finds a student by their business identifier (StudentID).
     *
     * @param studentId the unique business key (e.g., "STD-20260203-0001")
     * @return Optional containing the student if found, empty otherwise
     * @throws IllegalArgumentException if studentId is null or blank
     */
    Optional<Student> findByStudentId(String studentId);

    /**
     * Finds a student by their internal database ID.
     *
     * @param id the internal primary key
     * @return Optional containing the student if found, empty otherwise
     * @throws IllegalArgumentException if id is null
     */
    Optional<Student> findById(Long id);

    /**
     * Searches students by last name with partial match (case-insensitive).
     * Supports pagination for large result sets.
     *
     * @param lastName the last name to search (partial match supported)
     * @param pageable pagination parameters (page, size, sort)
     * @return Page of students matching the search criteria
     * @throws IllegalArgumentException if lastName is null or pageable is null
     */
    Page<Student> findByLastNameContaining(String lastName, Pageable pageable);

    /**
     * Searches students by status with pagination.
     *
     * @param status the student status to filter by (ACTIVE or INACTIVE)
     * @param pageable pagination parameters
     * @return Page of students with the specified status
     * @throws IllegalArgumentException if status or pageable is null
     */
    Page<Student> findByStatus(StudentStatus status, Pageable pageable);

    /**
     * Searches students by last name AND status with pagination.
     * Combined filter for more specific searches.
     *
     * @param lastName the last name to search (partial match)
     * @param status the student status to filter by
     * @param pageable pagination parameters
     * @return Page of students matching both criteria
     * @throws IllegalArgumentException if any parameter is null
     */
    Page<Student> findByLastNameContainingAndStatus(String lastName, StudentStatus status, Pageable pageable);

    /**
     * Checks if a mobile number already exists in the system.
     * Used for BR-2 (Mobile uniqueness) validation during registration.
     *
     * @param mobile the mobile number to check
     * @return true if mobile exists, false otherwise
     * @throws IllegalArgumentException if mobile is null
     */
    boolean existsByMobile(Mobile mobile);

    /**
     * Checks if a mobile number exists for any student except the specified one.
     * Used for BR-2 validation during profile updates.
     *
     * @param mobile the mobile number to check
     * @param id the ID of the student to exclude from the check
     * @return true if mobile exists for another student, false otherwise
     * @throws IllegalArgumentException if mobile or id is null
     */
    boolean existsByMobileAndIdNot(Mobile mobile, Long id);

    /**
     * Deletes a student by their internal ID.
     *
     * @param id the internal primary key
     * @throws IllegalArgumentException if id is null
     */
    void deleteById(Long id);

    /**
     * Counts total number of students in the system.
     *
     * @return total count of all students
     */
    long count();

    /**
     * Counts students with a specific status.
     *
     * @param status the status to count
     * @return count of students with the specified status
     * @throws IllegalArgumentException if status is null
     */
    long countByStatus(StudentStatus status);

    /**
     * Retrieves all students (use with caution - prefer paginated methods).
     *
     * @return Page of all students
     * @param pageable pagination parameters
     * @throws IllegalArgumentException if pageable is null
     */
    Page<Student> findAll(Pageable pageable);
}
