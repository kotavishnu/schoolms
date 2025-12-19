package com.school.management.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "fee_master")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fee_type", nullable = false, length = 100)
    private String feeType; // e.g., "Tuition", "Library", "Computer", "Special"

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "applicable_class_from", nullable = false)
    private Integer applicableClassFrom; // e.g., 1

    @Column(name = "applicable_class_to", nullable = false)
    private Integer applicableClassTo; // e.g., 10

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeeFrequency frequency;

    @Column(name = "academic_year", nullable = false, length = 20)
    private String academicYear;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isActive == null) {
            isActive = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
