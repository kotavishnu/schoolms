package com.school.management.repository;

import com.school.management.model.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for SchoolClass entity.
 *
 * Provides CRUD operations and custom queries for class management.
 *
 * @author School Management Team
 */
@Repository
public interface ClassRepository extends JpaRepository<SchoolClass, Long> {

    /**
     * Find classes by academic year
     * @param academicYear Academic year string (e.g., "2024-2025")
     * @return List of classes for the year
     */
    List<SchoolClass> findByAcademicYear(String academicYear);

    /**
     * Find class by class number, section, and academic year
     * @param classNumber Class number (1-10)
     * @param section Section identifier (A, B, C)
     * @param academicYear Academic year
     * @return Optional class
     */
    Optional<SchoolClass> findByClassNumberAndSectionAndAcademicYear(
            Integer classNumber, String section, String academicYear);

    /**
     * Find classes by class number (all sections)
     * @param classNumber Class number
     * @param academicYear Academic year
     * @return List of classes with different sections
     */
    List<SchoolClass> findByClassNumberAndAcademicYear(Integer classNumber, String academicYear);

    /**
     * Find all classes for academic year ordered by class number and section
     * @param academicYear Academic year
     * @return Ordered list of classes
     */
    @Query("SELECT c FROM SchoolClass c WHERE c.academicYear = :academicYear " +
           "ORDER BY c.classNumber, c.section")
    List<SchoolClass> findAllByAcademicYearOrdered(@Param("academicYear") String academicYear);

    /**
     * Find classes with available seats
     * @param academicYear Academic year
     * @return List of classes with capacity > current enrollment
     */
    @Query("SELECT c FROM SchoolClass c WHERE c.academicYear = :academicYear " +
           "AND c.currentEnrollment < c.capacity " +
           "ORDER BY c.classNumber, c.section")
    List<SchoolClass> findClassesWithAvailableSeats(@Param("academicYear") String academicYear);

    /**
     * Find classes that are almost full (>80% capacity)
     * @param academicYear Academic year
     * @return List of almost full classes
     */
    @Query("SELECT c FROM SchoolClass c WHERE c.academicYear = :academicYear " +
           "AND (CAST(c.currentEnrollment AS float) / CAST(c.capacity AS float)) >= 0.8 " +
           "ORDER BY c.classNumber, c.section")
    List<SchoolClass> findAlmostFullClasses(@Param("academicYear") String academicYear);

    /**
     * Check if class exists for given parameters
     * @param classNumber Class number
     * @param section Section
     * @param academicYear Academic year
     * @return true if class exists
     */
    boolean existsByClassNumberAndSectionAndAcademicYear(
            Integer classNumber, String section, String academicYear);

    /**
     * Count total students across all classes
     * @param academicYear Academic year
     * @return Total student count
     */
    @Query("SELECT SUM(c.currentEnrollment) FROM SchoolClass c WHERE c.academicYear = :academicYear")
    Long countTotalStudents(@Param("academicYear") String academicYear);
}
