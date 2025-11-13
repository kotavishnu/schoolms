package com.school.management.application.service;

import com.school.management.domain.academic.AcademicYear;
import com.school.management.domain.academic.ClassName;
import com.school.management.domain.academic.SchoolClass;
import com.school.management.infrastructure.persistence.AcademicYearRepository;
import com.school.management.infrastructure.persistence.SchoolClassRepository;
import com.school.management.shared.exception.BusinessException;
import com.school.management.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for School Class management
 *
 * Business Rules Enforced:
 * - Unique class-section-year combination
 * - Capacity constraints (1-100 students)
 * - Enrollment tracking
 *
 * @author School Management System
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SchoolClassService {

    private final SchoolClassRepository schoolClassRepository;
    private final AcademicYearRepository academicYearRepository;

    /**
     * Create a new school class
     * @param schoolClass the class to create
     * @return the created class
     */
    public SchoolClass createClass(SchoolClass schoolClass) {
        log.debug("Creating class: {}-{}", schoolClass.getClassName(), schoolClass.getSection());

        // Validate academic year exists
        if (schoolClass.getAcademicYear() == null || schoolClass.getAcademicYear().getId() == null) {
            throw new BusinessException("Academic year is required");
        }

        AcademicYear academicYear = academicYearRepository.findById(
            schoolClass.getAcademicYear().getId())
            .orElseThrow(() -> new NotFoundException("Academic year not found"));

        schoolClass.setAcademicYear(academicYear);

        // Check for duplicate class-section-year
        var existing = schoolClassRepository.findByClassNameAndSectionAndAcademicYear(
            schoolClass.getClassName(), schoolClass.getSection(), academicYear);

        if (existing.isPresent()) {
            throw new BusinessException(
                String.format("Class %s-%s already exists for academic year %s",
                    schoolClass.getClassName().getDisplayName(),
                    schoolClass.getSection(),
                    academicYear.getYearCode()));
        }

        SchoolClass saved = schoolClassRepository.save(schoolClass);
        log.info("Class created successfully: {}", saved.getFullClassName());

        return saved;
    }

    /**
     * Update a school class
     * @param classId the class ID
     * @param updates the updated class data
     * @return the updated class
     */
    public SchoolClass updateClass(Long classId, SchoolClass updates) {
        log.debug("Updating class: {}", classId);

        SchoolClass existing = schoolClassRepository.findById(classId)
            .orElseThrow(() -> new NotFoundException("Class not found with ID: " + classId));

        // Update allowed fields
        if (updates.getMaxCapacity() != null) {
            if (updates.getMaxCapacity() < existing.getCurrentEnrollment()) {
                throw new BusinessException(
                    "Cannot set max capacity below current enrollment");
            }
            existing.setMaxCapacity(updates.getMaxCapacity());
        }

        if (updates.getIsActive() != null) {
            existing.setIsActive(updates.getIsActive());
        }

        SchoolClass updated = schoolClassRepository.save(existing);
        log.info("Class updated successfully: {}", updated.getFullClassName());

        return updated;
    }

    /**
     * Get class by ID
     * @param classId the class ID
     * @return the class
     */
    @Transactional(readOnly = true)
    public SchoolClass getClassById(Long classId) {
        return schoolClassRepository.findById(classId)
            .orElseThrow(() -> new NotFoundException("Class not found with ID: " + classId));
    }

    /**
     * Get all classes for an academic year
     * @param academicYearId the academic year ID
     * @return list of classes
     */
    @Transactional(readOnly = true)
    public List<SchoolClass> getClassesByAcademicYear(Long academicYearId) {
        AcademicYear academicYear = academicYearRepository.findById(academicYearId)
            .orElseThrow(() -> new NotFoundException(
                "Academic year not found with ID: " + academicYearId));

        return schoolClassRepository.findByAcademicYear(academicYear);
    }

    /**
     * Get active classes for an academic year with pagination
     * @param academicYearId the academic year ID
     * @param pageable pagination parameters
     * @return page of classes
     */
    @Transactional(readOnly = true)
    public Page<SchoolClass> getActiveClasses(Long academicYearId, Pageable pageable) {
        AcademicYear academicYear = academicYearRepository.findById(academicYearId)
            .orElseThrow(() -> new NotFoundException(
                "Academic year not found with ID: " + academicYearId));

        return schoolClassRepository.findByAcademicYearAndIsActive(
            academicYear, true, pageable);
    }

    /**
     * Get classes with available capacity for enrollment
     * @param academicYearId the academic year ID
     * @return list of classes with capacity
     */
    @Transactional(readOnly = true)
    public List<SchoolClass> getClassesWithCapacity(Long academicYearId) {
        AcademicYear academicYear = academicYearRepository.findById(academicYearId)
            .orElseThrow(() -> new NotFoundException(
                "Academic year not found with ID: " + academicYearId));

        return schoolClassRepository.findClassesWithAvailableCapacity(academicYear);
    }

    /**
     * Get class by name, section, and academic year
     * @param className the class name
     * @param section the section
     * @param academicYearId the academic year ID
     * @return the class
     */
    @Transactional(readOnly = true)
    public SchoolClass getClassByDetails(ClassName className, String section, Long academicYearId) {
        AcademicYear academicYear = academicYearRepository.findById(academicYearId)
            .orElseThrow(() -> new NotFoundException(
                "Academic year not found with ID: " + academicYearId));

        return schoolClassRepository.findByClassNameAndSectionAndAcademicYear(
            className, section, academicYear)
            .orElseThrow(() -> new NotFoundException(
                String.format("Class %s-%s not found for academic year %s",
                    className.getDisplayName(), section, academicYear.getYearCode())));
    }

    /**
     * Delete a class
     * Only allowed if class has no enrollments
     * @param classId the class ID
     */
    public void deleteClass(Long classId) {
        log.debug("Deleting class: {}", classId);

        SchoolClass schoolClass = schoolClassRepository.findById(classId)
            .orElseThrow(() -> new NotFoundException("Class not found with ID: " + classId));

        if (schoolClass.getCurrentEnrollment() > 0) {
            throw new BusinessException(
                "Cannot delete class with active enrollments. Current enrollment: " +
                    schoolClass.getCurrentEnrollment());
        }

        schoolClassRepository.delete(schoolClass);
        log.info("Class deleted successfully: {}", schoolClass.getFullClassName());
    }

    /**
     * Get total enrollment for an academic year
     * @param academicYearId the academic year ID
     * @return total enrollment count
     */
    @Transactional(readOnly = true)
    public Long getTotalEnrollment(Long academicYearId) {
        AcademicYear academicYear = academicYearRepository.findById(academicYearId)
            .orElseThrow(() -> new NotFoundException(
                "Academic year not found with ID: " + academicYearId));

        Long total = schoolClassRepository.getTotalEnrollmentForYear(academicYear);
        return total != null ? total : 0L;
    }
}
