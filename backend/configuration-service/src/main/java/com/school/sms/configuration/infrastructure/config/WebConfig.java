package com.school.sms.configuration.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration for CORS and other MVC settings.
 *
 * <p>Configures CORS to allow requests from frontend development servers.</p>
 *
 * <p>Reference: LESSONS_LEARNED.md [D-001]</p>
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Value("${app.cors.allowed-methods}")
    private String allowedMethods;

    @Value("${app.cors.allowed-headers}")
    private String allowedHeaders;

    @Value("${app.cors.max-age}")
    private long maxAge;

    /**
     * Configures CORS mappings.
     *
     * <p>Allowed origins:</p>
     * <ul>
     *   <li>http://localhost:3000 - React dev server</li>
     *   <li>http://localhost:5173 - Vite dev server</li>
     * </ul>
     *
     * @param registry the CORS registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins(allowedOrigins.split(","))
            .allowedMethods(allowedMethods.split(","))
            .allowedHeaders(allowedHeaders.split(","))
            .allowCredentials(true)
            .maxAge(maxAge);
    }
}
