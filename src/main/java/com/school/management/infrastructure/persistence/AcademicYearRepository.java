package com.school.management.infrastructure.persistence;

import com.school.management.domain.academic.AcademicYear;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for AcademicYear entity
 *
 * @author School Management System
 * @version 1.0
 */
@Repository
public interface AcademicYearRepository extends BaseRepository<AcademicYear, Long> {

    /**
     * Find the current academic year
     * @param isCurrent true to find current year
     * @return optional academic year
     */
    Optional<AcademicYear> findByIsCurrent(Boolean isCurrent);

    /**
     * Find academic year by year code
     * @param yearCode the year code (e.g., "2024-2025")
     * @return optional academic year
     */
    Optional<AcademicYear> findByYearCode(String yearCode);

    /**
     * Find all academic years ordered by start date descending
     * @return list of academic years
     */
    @Query("SELECT ay FROM AcademicYear ay ORDER BY ay.startDate DESC")
    List<AcademicYear> findAllOrderByStartDateDesc();

    /**
     * Find academic years that overlap with given date range
     * @param startDate start date
     * @param endDate end date
     * @return list of overlapping academic years
     */
    @Query("SELECT ay FROM AcademicYear ay WHERE " +
        "(ay.startDate <= :endDate AND ay.endDate >= :startDate)")
    List<AcademicYear> findOverlappingYears(LocalDate startDate, LocalDate endDate);

    /**
     * Check if an academic year exists with given year code
     * @param yearCode the year code
     * @return true if exists, false otherwise
     */
    boolean existsByYearCode(String yearCode);
}
