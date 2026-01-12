package com.schoolms.configuration.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Database Configuration
 * Configures JPA repositories and transaction management
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.schoolms.configuration.infrastructure.persistence.repository")
@EnableTransactionManagement
public class DatabaseConfig {
    // HikariCP configuration is in application.yml
    // JPA properties are in application.yml
}
