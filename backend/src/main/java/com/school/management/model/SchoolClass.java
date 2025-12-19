package com.school.management.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "school_class", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"class_number", "section", "academic_year"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchoolClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "class_number", nullable = false)
    private Integer classNumber; // 1-10

    @Column(length = 10)
    private String section; // A, B, C, etc.

    @Column(name = "academic_year", nullable = false, length = 20)
    private String academicYear; // e.g., "2024-2025"

    @Column(nullable = false)
    private Integer capacity;

    @Column(name = "current_strength")
    private Integer currentStrength;

    @PrePersist
    protected void onCreate() {
        if (currentStrength == null) {
            currentStrength = 0;
        }
    }
}
