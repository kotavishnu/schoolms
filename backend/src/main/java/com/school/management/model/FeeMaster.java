package com.school.management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing fee master configuration for different fee types.
 *
 * Database Table: fee_master
 *
 * This entity defines the fee structure for various fee types (tuition, library, etc.)
 * with amount, frequency, and applicability date.
 *
 * Business Rules:
 * - Amount must be positive
 * - Fee configuration can change over academic years
 * - Applicable from date determines when fee becomes active
 *
 * @author School Management Team
 */
@Entity
@Table(name = "fee_master", indexes = {
    @Index(name = "idx_fee_type", columnList = "fee_type"),
    @Index(name = "idx_applicable_from", columnList = "applicable_from")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "fee_type", nullable = false, length = 30)
    @NotNull(message = "Fee type is required")
    private FeeType feeType;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    private BigDecimal amount;

    @Column(name = "frequency", length = 20)
    @Builder.Default
    private String frequency = "MONTHLY"; // MONTHLY, QUARTERLY, ANNUAL, ONE_TIME

    @Column(name = "applicable_from", nullable = false)
    @NotNull(message = "Applicable from date is required")
    private LocalDate applicableFrom;

    @Column(name = "applicable_to")
    private LocalDate applicableTo;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "academic_year", length = 20)
    private String academicYear;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Check if this fee master is currently applicable
     * @return true if current date is within applicable range
     */
    @Transient
    public boolean isCurrentlyApplicable() {
        LocalDate today = LocalDate.now();
        boolean afterStart = applicableFrom.isBefore(today) || applicableFrom.isEqual(today);
        boolean beforeEnd = applicableTo == null || applicableTo.isAfter(today) || applicableTo.isEqual(today);
        return isActive && afterStart && beforeEnd;
    }

    /**
     * Get display name for fee type
     * @return User-friendly fee type name
     */
    @Transient
    public String getFeeTypeName() {
        return feeType.toString().replace("_", " ");
    }
}
