package com.school.student.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generates unique student IDs in format: STD-YYYYMMDD-NNNN
 *
 * <p>Format:
 * <ul>
 *   <li>STD: Prefix</li>
 *   <li>YYYYMMDD: Current date</li>
 *   <li>NNNN: Sequential 4-digit number (resets daily)</li>
 * </ul>
 *
 * <p>Thread-safe implementation using AtomicInteger.
 * In production, consider using database sequences for distributed systems.
 */
@Component
@Slf4j
public class StudentIdGenerator {

    private static final String PREFIX = "STD";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final AtomicInteger sequence = new AtomicInteger(0);
    private String currentDate = getCurrentDateString();

    /**
     * Generate next student ID.
     * Format: STD-YYYYMMDD-NNNN
     *
     * @return unique student ID
     */
    public synchronized String generate() {
        String today = getCurrentDateString();

        // Reset sequence if date changed
        if (!today.equals(currentDate)) {
            log.info("Date changed from {} to {}, resetting sequence", currentDate, today);
            currentDate = today;
            sequence.set(0);
        }

        int nextSequence = sequence.incrementAndGet();
        String studentId = String.format("%s-%s-%04d", PREFIX, currentDate, nextSequence);

        log.debug("Generated student ID: {}", studentId);
        return studentId;
    }

    private String getCurrentDateString() {
        return LocalDate.now().format(DATE_FORMATTER);
    }
}
