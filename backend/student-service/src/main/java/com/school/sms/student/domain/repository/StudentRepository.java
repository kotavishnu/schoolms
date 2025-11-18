package com.school.sms.student.domain.repository;

import com.school.sms.student.domain.entity.Student;
import com.school.sms.student.domain.valueobject.StudentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository interface for Student aggregate.
 * This interface defines the contract for student persistence operations.
 */
public interface StudentRepository {

    /**
     * Saves a student entity.
     *
     * @param student the student to save
     * @return the saved student
     */
    Student save(Student student);

    /**
     * Finds a student by ID.
     *
     * @param studentId the student ID
     * @return Optional containing the student if found
     */
    Optional<Student> findById(String studentId);

    /**
     * Finds all students with pagination.
     *
     * @param pageable pagination information
     * @return page of students
     */
    Page<Student> findAll(Pageable pageable);

    /**
     * Finds students by status with pagination.
     *
     * @param status the student status
     * @param pageable pagination information
     * @return page of students with the specified status
     */
    Page<Student> findByStatus(StudentStatus status, Pageable pageable);

    /**
     * Finds students by last name (starts with) with pagination.
     *
     * @param lastName the last name prefix
     * @param pageable pagination information
     * @return page of students with matching last name
     */
    Page<Student> findByLastNameStartingWithIgnoreCase(String lastName, Pageable pageable);

    /**
     * Deletes a student by ID (soft delete by setting status to INACTIVE).
     *
     * @param studentId the student ID
     */
    void deleteById(String studentId);

    /**
     * Checks if a student exists with the given mobile number.
     *
     * @param mobile the mobile number
     * @return true if exists, false otherwise
     */
    boolean existsByMobile(String mobile);

    /**
     * Checks if a student exists with the given mobile number excluding a specific student ID.
     * Useful for update operations to check for duplicate mobile numbers.
     *
     * @param mobile the mobile number
     * @param studentId the student ID to exclude
     * @return true if exists, false otherwise
     */
    boolean existsByMobileAndStudentIdNot(String mobile, String studentId);

    /**
     * Finds all students (without pagination).
     *
     * @return list of all students
     */
    List<Student> findAll();
}
