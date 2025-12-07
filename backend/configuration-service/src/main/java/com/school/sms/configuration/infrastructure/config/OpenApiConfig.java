package com.school.sms.configuration.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI (Swagger) configuration.
 *
 * <p>Configures Swagger UI and API documentation.</p>
 *
 * <p>SpringDoc OpenAPI version: 2.7.0 (compatible with Spring Boot 3.5.0)</p>
 * <p>Reference: LESSONS_LEARNED.md [D-001]</p>
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configures OpenAPI documentation metadata.
     *
     * @return the OpenAPI configuration
     */
    @Bean
    public OpenAPI configurationServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Configuration Service API")
                .description("Configuration Management Microservice - Handles school configuration " +
                             "settings grouped by category (GENERAL, ACADEMIC, FINANCIAL)")
                .version("1.0.0")
                .contact(new Contact()
                    .name("School Management System Team")
                    .email("support@school.com"))
                .license(new License()
                    .name("Proprietary")
                    .url("https://www.school.com/license")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8082")
                    .description("Local Development Server")
            ));
    }
}
