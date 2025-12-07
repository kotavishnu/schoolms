package com.school.sms.configuration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for Configuration Service microservice.
 *
 * <p>This service handles school configuration management as key-value
 * settings grouped by categories (GENERAL, ACADEMIC, FINANCIAL).</p>
 *
 * <p>Port: 8082</p>
 * <p>Database: sms_config_db</p>
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
@SpringBootApplication
public class ConfigurationServiceApplication {

    /**
     * Main entry point for the Configuration Service application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(ConfigurationServiceApplication.class, args);
    }
}
