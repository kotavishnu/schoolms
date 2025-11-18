package com.school.sms.eureka;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Integration test for Eureka Server Application.
 * Ensures the application context loads successfully.
 */
@SpringBootTest
@TestPropertySource(properties = {
    "eureka.client.register-with-eureka=false",
    "eureka.client.fetch-registry=false"
})
class EurekaServerApplicationTest {

    @Test
    void contextLoads() {
        // This test ensures the Spring application context loads successfully
        // If the context fails to load, this test will fail
    }
}
