package com.school.management.infrastructure.persistence;

import com.school.management.domain.academic.AcademicYear;
import com.school.management.domain.academic.ClassName;
import com.school.management.domain.academic.SchoolClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for SchoolClass entity
 *
 * @author School Management System
 * @version 1.0
 */
@Repository
public interface SchoolClassRepository extends BaseRepository<SchoolClass, Long> {

    /**
     * Find all classes for an academic year
     * @param academicYear the academic year
     * @return list of classes
     */
    List<SchoolClass> findByAcademicYear(AcademicYear academicYear);

    /**
     * Find class by name, section, and academic year
     * @param className the class name
     * @param section the section
     * @param academicYear the academic year
     * @return optional school class
     */
    Optional<SchoolClass> findByClassNameAndSectionAndAcademicYear(
        ClassName className, String section, AcademicYear academicYear);

    /**
     * Find active classes for an academic year with pagination
     * @param academicYear the academic year
     * @param isActive active status
     * @param pageable pagination parameters
     * @return page of classes
     */
    Page<SchoolClass> findByAcademicYearAndIsActive(
        AcademicYear academicYear, Boolean isActive, Pageable pageable);

    /**
     * Find classes by class name
     * @param className the class name
     * @return list of classes
     */
    List<SchoolClass> findByClassName(ClassName className);

    /**
     * Find classes with available capacity
     * @param academicYear the academic year
     * @return list of classes with capacity
     */
    @Query("SELECT sc FROM SchoolClass sc WHERE sc.academicYear = :academicYear " +
        "AND sc.currentEnrollment < sc.maxCapacity AND sc.isActive = true")
    List<SchoolClass> findClassesWithAvailableCapacity(@Param("academicYear") AcademicYear academicYear);

    /**
     * Count classes for an academic year
     * @param academicYear the academic year
     * @return count of classes
     */
    long countByAcademicYear(AcademicYear academicYear);

    /**
     * Find classes by academic year ID
     * @param academicYearId the academic year ID
     * @return list of classes
     */
    @Query("SELECT sc FROM SchoolClass sc WHERE sc.academicYear.id = :academicYearId")
    List<SchoolClass> findByAcademicYearId(@Param("academicYearId") Long academicYearId);

    /**
     * Get total enrollment count for an academic year
     * @param academicYear the academic year
     * @return total enrollment count
     */
    @Query("SELECT SUM(sc.currentEnrollment) FROM SchoolClass sc " +
        "WHERE sc.academicYear = :academicYear")
    Long getTotalEnrollmentForYear(@Param("academicYear") AcademicYear academicYear);
}
