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
 * Entity representing fee journal for tracking student fee dues and payments.
 *
 * Database Table: fee_journal
 *
 * This entity maintains a monthly record of fee dues, payments, and pending amounts
 * for each student.
 *
 * Business Rules:
 * - One journal entry per student per month
 * - Amount due is calculated based on class and fee master
 * - Amount paid is updated when receipt is generated
 * - Balance is calculated as (due - paid)
 *
 * @author School Management Team
 */
@Entity
@Table(name = "fee_journal", indexes = {
    @Index(name = "idx_student_id", columnList = "student_id"),
    @Index(name = "idx_month_year", columnList = "month, year"),
    @Index(name = "idx_status", columnList = "status")
}, uniqueConstraints = {
    @UniqueConstraint(columnNames = {"student_id", "month", "year"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeJournal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @NotNull(message = "Student is required")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Student student;

    @Column(name = "month", nullable = false, length = 20)
    @NotBlank(message = "Month is required")
    private String month; // "January", "February", etc.

    @Column(name = "year", nullable = false)
    @NotNull(message = "Year is required")
    @Min(value = 2000, message = "Year must be valid")
    private Integer year;

    @Column(name = "amount_due", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Amount due is required")
    @DecimalMin(value = "0.0", message = "Amount due must be non-negative")
    @Builder.Default
    private BigDecimal amountDue = BigDecimal.ZERO;

    @Column(name = "amount_paid", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal amountPaid = BigDecimal.ZERO;

    @Column(name = "balance", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receipt_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private FeeReceipt receipt;

    @Column(name = "remarks", length = 500)
    private String remarks;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Calculate and update balance
     */
    @PrePersist
    @PreUpdate
    public void calculateBalance() {
        if (amountDue != null && amountPaid != null) {
            this.balance = amountDue.subtract(amountPaid);

            // Update status based on payment
            if (balance.compareTo(BigDecimal.ZERO) == 0) {
                this.status = PaymentStatus.PAID;
            } else if (amountPaid.compareTo(BigDecimal.ZERO) > 0) {
                this.status = PaymentStatus.PARTIAL;
            } else if (dueDate != null && LocalDate.now().isAfter(dueDate)) {
                this.status = PaymentStatus.OVERDUE;
            } else {
                this.status = PaymentStatus.PENDING;
            }
        }
    }

    /**
     * Check if payment is overdue
     * @return true if payment is past due date
     */
    @Transient
    public boolean isOverdue() {
        return dueDate != null && LocalDate.now().isAfter(dueDate) &&
               balance != null && balance.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Get month-year display string
     * @return Formatted string (e.g., "January 2025")
     */
    @Transient
    public String getMonthYearDisplay() {
        return month + " " + year;
    }
}
