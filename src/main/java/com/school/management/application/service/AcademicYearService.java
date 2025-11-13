package com.school.management.application.service;

import com.school.management.domain.academic.AcademicYear;
import com.school.management.infrastructure.persistence.AcademicYearRepository;
import com.school.management.shared.exception.BusinessException;
import com.school.management.shared.exception.NotFoundException;
import com.school.management.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service class for Academic Year management
 *
 * Business Rules Enforced:
 * - Only one current academic year at a time
 * - No overlapping academic years
 * - Year code must match date range
 *
 * @author School Management System
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AcademicYearService {

    private final AcademicYearRepository academicYearRepository;

    /**
     * Create a new academic year
     * @param academicYear the academic year to create
     * @return the created academic year
     */
    public AcademicYear createAcademicYear(AcademicYear academicYear) {
        log.debug("Creating academic year: {}", academicYear.getYearCode());

        // Validate year code uniqueness
        if (academicYearRepository.existsByYearCode(academicYear.getYearCode())) {
            throw new BusinessException(
                "Academic year already exists with code: " + academicYear.getYearCode());
        }

        // Check for overlapping years
        List<AcademicYear> overlapping = academicYearRepository.findOverlappingYears(
            academicYear.getStartDate(), academicYear.getEndDate());

        if (!overlapping.isEmpty()) {
            throw new BusinessException(
                "Academic year overlaps with existing year: " + overlapping.get(0).getYearCode());
        }

        // If this is set as current, update existing current to false
        if (Boolean.TRUE.equals(academicYear.getIsCurrent())) {
            updateCurrentAcademicYear(null);
        }

        AcademicYear saved = academicYearRepository.save(academicYear);
        log.info("Academic year created successfully: {}", saved.getYearCode());

        return saved;
    }

    /**
     * Get current academic year
     * @return optional current academic year
     */
    @Transactional(readOnly = true)
    public Optional<AcademicYear> getCurrentAcademicYear() {
        return academicYearRepository.findByIsCurrent(true);
    }

    /**
     * Set an academic year as current
     * Automatically unsets any existing current year
     * @param academicYearId the ID of the year to set as current
     * @return the updated academic year
     */
    public AcademicYear setCurrentAcademicYear(Long academicYearId) {
        log.debug("Setting academic year as current: {}", academicYearId);

        AcademicYear academicYear = academicYearRepository.findById(academicYearId)
            .orElseThrow(() -> new NotFoundException(
                "Academic year not found with ID: " + academicYearId));

        // Unset existing current year
        updateCurrentAcademicYear(null);

        // Set new current year
        academicYear.setIsCurrent(true);
        AcademicYear updated = academicYearRepository.save(academicYear);

        log.info("Academic year set as current: {}", updated.getYearCode());
        return updated;
    }

    /**
     * Update academic year
     * @param academicYearId the academic year ID
     * @param updates the updated academic year data
     * @return the updated academic year
     */
    public AcademicYear updateAcademicYear(Long academicYearId, AcademicYear updates) {
        log.debug("Updating academic year: {}", academicYearId);

        AcademicYear existing = academicYearRepository.findById(academicYearId)
            .orElseThrow(() -> new NotFoundException(
                "Academic year not found with ID: " + academicYearId));

        // Update fields
        if (updates.getStartDate() != null) {
            existing.setStartDate(updates.getStartDate());
        }
        if (updates.getEndDate() != null) {
            existing.setEndDate(updates.getEndDate());
        }
        if (updates.getYearCode() != null && !updates.getYearCode().equals(existing.getYearCode())) {
            if (academicYearRepository.existsByYearCode(updates.getYearCode())) {
                throw new BusinessException(
                    "Academic year already exists with code: " + updates.getYearCode());
            }
            existing.setYearCode(updates.getYearCode());
        }

        AcademicYear updated = academicYearRepository.save(existing);
        log.info("Academic year updated successfully: {}", updated.getYearCode());

        return updated;
    }

    /**
     * Get all academic years ordered by start date
     * @return list of academic years
     */
    @Transactional(readOnly = true)
    public List<AcademicYear> getAllAcademicYears() {
        return academicYearRepository.findAllOrderByStartDateDesc();
    }

    /**
     * Get academic year by ID
     * @param academicYearId the academic year ID
     * @return the academic year
     */
    @Transactional(readOnly = true)
    public AcademicYear getAcademicYearById(Long academicYearId) {
        return academicYearRepository.findById(academicYearId)
            .orElseThrow(() -> new NotFoundException(
                "Academic year not found with ID: " + academicYearId));
    }

    /**
     * Get academic year by year code
     * @param yearCode the year code
     * @return the academic year
     */
    @Transactional(readOnly = true)
    public AcademicYear getAcademicYearByCode(String yearCode) {
        return academicYearRepository.findByYearCode(yearCode)
            .orElseThrow(() -> new NotFoundException(
                "Academic year not found with code: " + yearCode));
    }

    /**
     * Delete academic year
     * @param academicYearId the academic year ID
     */
    public void deleteAcademicYear(Long academicYearId) {
        log.debug("Deleting academic year: {}", academicYearId);

        AcademicYear academicYear = academicYearRepository.findById(academicYearId)
            .orElseThrow(() -> new NotFoundException(
                "Academic year not found with ID: " + academicYearId));

        // Don't allow deletion of current year
        if (Boolean.TRUE.equals(academicYear.getIsCurrent())) {
            throw new BusinessException("Cannot delete the current academic year");
        }

        academicYearRepository.delete(academicYear);
        log.info("Academic year deleted successfully: {}", academicYear.getYearCode());
    }

    /**
     * Helper method to unset current academic year
     * @param exceptId ID to except from update (can be null)
     */
    private void updateCurrentAcademicYear(Long exceptId) {
        Optional<AcademicYear> current = academicYearRepository.findByIsCurrent(true);
        if (current.isPresent() && (exceptId == null || !current.get().getId().equals(exceptId))) {
            AcademicYear currentYear = current.get();
            currentYear.setIsCurrent(false);
            academicYearRepository.save(currentYear);
            log.debug("Unset current status for academic year: {}", currentYear.getYearCode());
        }
    }
}
