package com.school.sms.student.infrastructure.util;

import org.springframework.stereotype.Component;

import java.time.Year;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Utility class for generating unique student IDs.
 * Format: STU-YYYY-NNNNN (e.g., STU-2025-00001)
 */
@Component
public class StudentIdGenerator {

    private final AtomicLong counter = new AtomicLong(1);

    /**
     * Generates a new unique student ID.
     * Format: STU-{YEAR}-{5-digit sequence}
     *
     * @return generated student ID
     */
    public String generate() {
        int currentYear = Year.now().getValue();
        long sequence = counter.getAndIncrement();
        return String.format("STU-%d-%05d", currentYear, sequence);
    }

    /**
     * Resets the counter (useful for testing).
     */
    public void reset() {
        counter.set(1);
    }
}
