package com.school.sms.student;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for Student Service microservice.
 *
 * <p>This service handles student registration, profile management,
 * and enrollment history tracking for the School Management System.</p>
 *
 * <p>Port: 8081</p>
 * <p>Database: sms_student_db</p>
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
@SpringBootApplication
public class StudentServiceApplication {

    /**
     * Main entry point for the Student Service application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(StudentServiceApplication.class, args);
    }
}
