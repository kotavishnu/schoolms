package com.schoolms.student.infrastructure.generator;

import com.schoolms.student.infrastructure.persistence.repository.StudentJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

/**
 * Student ID Generator (BE-017)
 * Generates auto-increment student IDs in format STU-YYYY-NNNNN
 * Thread-safe implementation
 */
@Component
public class StudentIdGenerator {

    private final StudentJpaRepository studentJpaRepository;

    public StudentIdGenerator(StudentJpaRepository studentJpaRepository) {
        this.studentJpaRepository = studentJpaRepository;
    }

    /**
     * Generates the next student ID for the current year
     * Format: STU-YYYY-NNNNN (e.g., STU-2026-00001)
     */
    @Transactional
    public synchronized String generateNextStudentId() {
        int currentYear = LocalDate.now().getYear();
        String yearPrefix = "STU-" + currentYear + "-";

        // Find all student IDs for the current year
        List<String> existingIds = studentJpaRepository.findAll().stream()
            .map(student -> student.getStudentId())
            .filter(id -> id != null && id.startsWith(yearPrefix))
            .toList();

        int maxSequence = 0;
        for (String id : existingIds) {
            try {
                String sequencePart = id.substring(yearPrefix.length());
                int sequence = Integer.parseInt(sequencePart);
                if (sequence > maxSequence) {
                    maxSequence = sequence;
                }
            } catch (Exception e) {
                // Skip invalid IDs
            }
        }

        int nextSequence = maxSequence + 1;
        return String.format("STU-%04d-%05d", currentYear, nextSequence);
    }
}
