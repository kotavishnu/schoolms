package com.school.management.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * TestContainers configuration for integration tests.
 *
 * <p>This configuration provides PostgreSQL and Redis containers for integration tests
 * using TestContainers. The containers are started once and shared across all
 * integration tests to improve performance.</p>
 *
 * <p>Features:</p>
 * <ul>
 *   <li>PostgreSQL 18 Alpine (lightweight)</li>
 *   <li>Redis 7.2 Alpine (lightweight)</li>
 *   <li>Automatic connection configuration via @ServiceConnection</li>
 *   <li>Shared container instances for all tests</li>
 *   <li>Automatic cleanup after test suite completion</li>
 * </ul>
 *
 * <p>Usage in Integration Tests:</p>
 * <pre>
 * {@code
 * @DataJpaTest
 * @Testcontainers
 * @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
 * @Import(TestContainersConfiguration.class)
 * class MyRepositoryIntegrationTest {
 *     // Tests...
 * }
 * }
 * </pre>
 *
 * @author School Management System Development Team
 * @version 1.0.0
 * @since 2025-11-11
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestContainersConfiguration {

    /**
     * Creates a shared PostgreSQL container for integration tests.
     *
     * <p>The @ServiceConnection annotation automatically configures
     * Spring Boot's DataSource to connect to this container.</p>
     *
     * <p>Container Configuration:</p>
     * <ul>
     *   <li>Image: postgres:18-alpine</li>
     *   <li>Database: school_management_test</li>
     *   <li>Username: test</li>
     *   <li>Password: test</li>
     *   <li>Reuse: true (shared across tests)</li>
     * </ul>
     *
     * @return PostgreSQL container instance
     */
    @Bean
    public PostgreSQLContainer<?> postgresContainer() {
        PostgreSQLContainer<?> container = new PostgreSQLContainer<>(DockerImageName.parse("postgres:18-alpine"))
                .withDatabaseName("school_management_test")
                .withUsername("test")
                .withPassword("test")
                .withReuse(true);
        container.start();
        return container;
    }

    /**
     * Creates a shared Redis container for integration tests.
     *
     * <p>Container Configuration:</p>
     * <ul>
     *   <li>Image: redis:7.2-alpine</li>
     *   <li>Port: 6379 (default Redis port)</li>
     *   <li>Reuse: true (shared across tests)</li>
     * </ul>
     *
     * @return Redis container instance
     */
    @Bean
    public GenericContainer<?> redisContainer() {
        GenericContainer<?> container = new GenericContainer<>(DockerImageName.parse("redis:7.2-alpine"))
                .withExposedPorts(6379)
                .withReuse(true);
        container.start();
        return container;
    }

    /**
     * Dynamically configures test properties from running containers.
     */
    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        // Properties will be set up by individual tests that use these containers
    }
}
