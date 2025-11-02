package com.school.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main Spring Boot Application class for School Management System.
 *
 * This application provides comprehensive school management functionality including:
 * - Student registration and management
 * - Class management with capacity tracking
 * - Fee master configuration
 * - Fee receipt generation with Drools rules engine
 * - Fee journal tracking for payments and dues
 * - School configuration management
 *
 * @author School Management Team
 * @version 1.0.0
 * @since 2024-10-26
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableTransactionManagement
public class SchoolManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchoolManagementApplication.class, args);
    }
}
