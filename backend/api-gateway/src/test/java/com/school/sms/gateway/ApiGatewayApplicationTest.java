package com.school.sms.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Integration test for API Gateway Application.
 * Ensures the application context loads successfully.
 */
@SpringBootTest
@TestPropertySource(properties = {
    "eureka.client.enabled=false"
})
class ApiGatewayApplicationTest {

    @Test
    void contextLoads() {
        // This test ensures the Spring application context loads successfully
    }
}
