package com.school.sms.configuration.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI configuration for Configuration Service.
 */
@Configuration
public class OpenAPIConfig {

    @Value("${server.port:8082}")
    private int serverPort;

    @Bean
    public OpenAPI configurationServiceAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Configuration Service API")
                        .description("Configuration and School Profile Management Service for School Management System")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("SMS Development Team")
                                .email("dev@school.com")
                                .url("https://school.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080/api/v1")
                                .description("API Gateway"),
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Direct Service Access")
                ));
    }
}
