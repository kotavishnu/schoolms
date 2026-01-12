package com.schoolms.configuration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Configuration Service Application
 * Main entry point for the Configuration Management Microservice
 *
 * @version 1.0
 * @since 2026-01-08
 */
@SpringBootApplication
public class ConfigurationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigurationServiceApplication.class, args);
    }
}
