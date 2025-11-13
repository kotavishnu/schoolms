package com.school.management.domain.student;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generator for unique student codes
 * Format: STU-YYYY-NNNNN (e.g., STU-2025-00001)
 * Thread-safe implementation using AtomicInteger
 *
 * @author School Management System
 * @version 1.0
 */
@Component
public class StudentCodeGenerator {

    private final AtomicInteger sequence = new AtomicInteger(1);

    /**
     * Generates a unique student code
     * Format: STU-YYYY-NNNNN
     * where YYYY is the current year and NNNNN is a zero-padded 5-digit sequence number
     *
     * @return generated student code
     */
    public String generate() {
        int year = LocalDate.now().getYear();
        int seq = sequence.getAndIncrement();
        return String.format("STU-%d-%05d", year, seq);
    }

    /**
     * Resets the sequence to 1
     * Used for testing or year-end rollover
     */
    public void reset() {
        sequence.set(1);
    }

    /**
     * Gets the current sequence value without incrementing
     *
     * @return current sequence value
     */
    public int getCurrentSequence() {
        return sequence.get();
    }
}
