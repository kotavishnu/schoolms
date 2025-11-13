package com.school.management.domain.academic;

import com.school.management.domain.base.BaseEntity;
import com.school.management.shared.exception.ValidationException;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * AcademicYear domain entity
 * Represents an academic year (e.g., 2024-2025)
 *
 * Business Rules:
 * - Only one academic year can be current at a time
 * - Year code format: YYYY-YYYY (e.g., 2024-2025)
 * - End date must be after start date
 *
 * @author School Management System
 * @version 1.0
 */
@Entity
@Table(name = "academic_years", indexes = {
    @Index(name = "idx_academic_years_year_code", columnList = "year_code"),
    @Index(name = "idx_academic_years_current", columnList = "is_current")
})
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class AcademicYear extends BaseEntity {

    @Column(nullable = false, unique = true, length = 10, name = "year_code")
    @Pattern(regexp = "^\\d{4}-\\d{4}$", message = "Year code must be in format YYYY-YYYY")
    private String yearCode;

    @Column(nullable = false, name = "start_date")
    private LocalDate startDate;

    @Column(nullable = false, name = "end_date")
    private LocalDate endDate;

    @Column(nullable = false, columnDefinition = "boolean default false", name = "is_current")
    private Boolean isCurrent = false;

    @OneToMany(mappedBy = "academicYear", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SchoolClass> classes = new HashSet<>();

    /**
     * Validate date range before persistence
     */
    @PrePersist
    @PreUpdate
    private void validateDateRange() {
        if (endDate != null && startDate != null && endDate.isBefore(startDate)) {
            throw new ValidationException("End date must be after start date");
        }

        // Validate year code format matches dates
        if (yearCode != null && startDate != null) {
            String[] years = yearCode.split("-");
            if (years.length == 2) {
                int startYear = startDate.getYear();
                int codeStartYear = Integer.parseInt(years[0]);
                if (startYear != codeStartYear) {
                    throw new ValidationException(
                        "Year code must match start date year");
                }
            }
        }
    }

    /**
     * Check if this academic year is currently active
     * @return true if current, false otherwise
     */
    public boolean isActive() {
        return Boolean.TRUE.equals(isCurrent);
    }

    /**
     * Get the duration of the academic year in days
     * @return number of days
     */
    public long getDurationInDays() {
        if (startDate != null && endDate != null) {
            return java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
        }
        return 0;
    }
}
