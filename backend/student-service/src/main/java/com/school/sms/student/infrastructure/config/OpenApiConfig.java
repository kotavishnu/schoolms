package com.school.sms.student.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI studentServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Student Service API")
                        .description("REST API for Student Registration and Management")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("School Management System Team")
                                .email("support@school.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8081")
                                .description("Local Student Service"),
                        new Server()
                                .url("http://localhost:8080/api/v1")
                                .description("API Gateway")
                ));
    }
}
