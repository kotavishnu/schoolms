package com.school.student;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Student Service - Main Application
 * School Management System
 *
 * Microservice for Student Management
 * Port: 8081
 * Database: student_db (PostgreSQL 18+)
 * Cache: Redis DB 0
 */
@SpringBootApplication
@EnableCaching
@EnableJpaAuditing
public class StudentServiceApplication {

    public static void main(String[] args) {
        // Set timezone to UTC (D-010: PostgreSQL compatibility)
        System.setProperty("user.timezone", "UTC");
        SpringApplication.run(StudentServiceApplication.class, args);
    }
}
