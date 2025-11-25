package com.school.sms.configuration.presentation.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger Configuration
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI configurationServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Configuration Service API")
                        .version("1.0.0")
                        .description("School Management System - Configuration Service APIs for managing school settings")
                        .contact(new Contact()
                                .name("SMS Development Team")
                                .email("dev@sms.example.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8082").description("Development Server"),
                        new Server().url("https://api.sms.example.com").description("Production Server")
                ));
    }
}
