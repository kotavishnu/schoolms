package com.school.sms.student.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

/**
 * OpenAPI/Swagger configuration for API documentation.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI studentServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Student Service API")
                .description("REST API for Student Management System - Phase 1")
                .version("1.0.0")
                .contact(new Contact()
                    .name("SMS Development Team")
                    .email("support@sms.com"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
            .servers(List.of(
                new Server().url("http://localhost:8081").description("Development Server"),
                new Server().url("https://api.sms.com").description("Production Server")
            ));
    }
}
