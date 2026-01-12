package com.schoolms.student.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Database Configuration (BE-016)
 * Configures JPA repositories and transaction management
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.schoolms.student.infrastructure.persistence.repository")
@EnableTransactionManagement
public class DatabaseConfig {
    // HikariCP configuration is in application.yml
    // JPA properties are in application.yml
}
