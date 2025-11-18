package com.school.sms.student;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Student Service Application for School Management System.
 * Handles student registration, profile management, and related operations.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaAuditing
public class StudentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentServiceApplication.class, args);
    }
}
