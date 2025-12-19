package com.school.management.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "fee_journal")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeJournal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "fee_receipt_id")
    private Long feeReceiptId;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "month_paid", length = 20)
    private String monthPaid; // e.g., "January 2024"

    @Column(name = "is_pending", nullable = false)
    private Boolean isPending;

    @Column(name = "academic_year", nullable = false, length = 20)
    private String academicYear;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isPending == null) {
            isPending = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
