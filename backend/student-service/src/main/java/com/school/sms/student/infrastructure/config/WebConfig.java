package com.school.sms.student.infrastructure.config;

import org.springframework.context.annotation.Configuration;

/**
 * Web configuration for CORS and other web-related settings.
 *
 * NOTE: CORS is now handled by the API Gateway, so this configuration is disabled
 * to prevent duplicate CORS headers.
 */
@Configuration
public class WebConfig {

    // CORS configuration removed - handled by API Gateway
    // This prevents duplicate Access-Control-Allow-Origin headers

}
