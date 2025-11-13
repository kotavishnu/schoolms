package com.school.management.infrastructure.persistence;

import com.school.management.domain.student.Guardian;
import com.school.management.domain.student.Relationship;
import com.school.management.domain.student.Student;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Guardian entity
 * Provides custom query methods for guardian-related database operations
 *
 * @author School Management System
 * @version 1.0
 */
@Repository
public interface GuardianRepository extends BaseRepository<Guardian, Long> {

    /**
     * Find all guardians for a specific student
     *
     * @param student the student entity
     * @return list of guardians
     */
    List<Guardian> findByStudent(Student student);

    /**
     * Find all guardians for a specific student by student ID
     *
     * @param studentId the student ID
     * @return list of guardians
     */
    @Query("SELECT g FROM Guardian g WHERE g.student.id = :studentId")
    List<Guardian> findByStudentId(@Param("studentId") Long studentId);

    /**
     * Find guardian by student and relationship type
     * Used to enforce one guardian per relationship type per student
     *
     * @param student the student entity
     * @param relationship the relationship type
     * @return optional guardian
     */
    Optional<Guardian> findByStudentAndRelationship(Student student, Relationship relationship);

    /**
     * Find primary guardian for a student
     *
     * @param student the student entity
     * @param isPrimary true to find primary guardian
     * @return optional primary guardian
     */
    Optional<Guardian> findByStudentAndIsPrimary(Student student, Boolean isPrimary);

    /**
     * Count total guardians for a student
     * Used to enforce at least one guardian per student
     *
     * @param student the student entity
     * @return count of guardians
     */
    long countByStudent(Student student);

    /**
     * Check if guardian exists with given mobile hash
     * Used for mobile number uniqueness validation
     *
     * @param mobileHash SHA-256 hash of mobile number
     * @return true if exists, false otherwise
     */
    boolean existsByMobileHash(String mobileHash);

    /**
     * Find guardian by mobile hash and student (for uniqueness within student context)
     *
     * @param mobileHash the mobile hash
     * @param student the student entity
     * @return optional guardian
     */
    Optional<Guardian> findByMobileHashAndStudent(String mobileHash, Student student);
}
