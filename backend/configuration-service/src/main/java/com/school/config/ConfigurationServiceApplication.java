package com.school.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Configuration Service Application.
 * Manages school configuration settings with Redis caching.
 *
 * @author Backend Developer Agent
 * @version 1.0.0
 * @since 2026-02-04
 */
@SpringBootApplication
@EnableCaching
public class ConfigurationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigurationServiceApplication.class, args);
    }
}
