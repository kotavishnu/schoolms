package com.school.configuration.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS Configuration
 *
 * Configures Cross-Origin Resource Sharing for frontend integration.
 *
 * Allowed Origins (D-002):
 * - http://localhost:5173 (Vite dev server - primary)
 * - http://localhost:5174 (Vite dev server - alternate)
 * - http://localhost:5175 (Vite dev server - alternate)
 * - http://localhost:3000 (React dev server - alternate)
 * - http://localhost:4173 (Vite preview)
 *
 * Security Note:
 * This configuration is for development only.
 * In production, restrict origins to specific domains.
 */
@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins(
                        "http://localhost:5173",  // Vite (primary)
                        "http://localhost:5174",  // Vite (alternate)
                        "http://localhost:5175",  // Vite (alternate)
                        "http://localhost:3000",  // React/CRA
                        "http://localhost:4173"   // Vite preview
                    )
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .allowCredentials(true)
                    .maxAge(3600); // Cache preflight response for 1 hour
            }
        };
    }
}
