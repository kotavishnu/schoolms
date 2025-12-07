package com.school.sms.student.domain.repository;

import com.school.sms.student.domain.model.Student;
import com.school.sms.student.domain.model.StudentId;
import com.school.sms.student.domain.model.StudentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Repository interface for Student domain entity.
 *
 * <p>This is a domain-level contract. The infrastructure layer
 * will provide the concrete implementation using JPA.</p>
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
public interface StudentRepository {

    /**
     * Saves a student (create or update).
     *
     * @param student the student to save
     * @return the saved student with assigned ID and version
     */
    Student save(Student student);

    /**
     * Finds a student by their database ID.
     *
     * @param id the database ID
     * @return an Optional containing the student if found
     */
    Optional<Student> findById(Long id);

    /**
     * Finds a student by their business key (Student ID).
     *
     * @param studentId the student ID
     * @return an Optional containing the student if found
     */
    Optional<Student> findByStudentId(StudentId studentId);

    /**
     * Finds a student by mobile number.
     *
     * @param mobile the mobile number
     * @return an Optional containing the student if found
     */
    Optional<Student> findByMobile(String mobile);

    /**
     * Finds students by last name (paginated).
     *
     * @param lastName the last name to search for
     * @param pageable pagination information
     * @return a page of students matching the last name
     */
    Page<Student> findByLastName(String lastName, Pageable pageable);

    /**
     * Finds students by father's name (paginated).
     *
     * @param fathersName the father's name to search for
     * @param pageable pagination information
     * @return a page of students matching the father's name
     */
    Page<Student> findByFathersName(String fathersName, Pageable pageable);

    /**
     * Finds students by status (paginated).
     *
     * @param status the student status
     * @param pageable pagination information
     * @return a page of students with the given status
     */
    Page<Student> findByStatus(StudentStatus status, Pageable pageable);

    /**
     * Finds all students (paginated).
     *
     * @param pageable pagination information
     * @return a page of students
     */
    Page<Student> findAll(Pageable pageable);

    /**
     * Deletes a student by their database ID.
     *
     * @param id the database ID
     */
    void deleteById(Long id);

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
    long countActiveStudents();
}
