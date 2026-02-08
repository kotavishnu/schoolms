package com.school.student.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

/**
 * HTTP interceptor to manage correlation IDs for request tracing.
 *
 * <p>Behavior:
 * <ul>
 *   <li>Extracts correlation ID from X-Correlation-ID header if present</li>
 *   <li>Generates new UUID if header is missing</li>
 *   <li>Adds correlation ID to SLF4J MDC for logging</li>
 *   <li>Adds correlation ID to response header</li>
 *   <li>Cleans up MDC after request completion</li>
 * </ul>
 *
 * <p>Usage in logs:
 * <pre>
 * logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} [%X{correlationId}] %-5level %logger{36} - %msg%n
 * </pre>
 */
@Component
@Slf4j
public class CorrelationInterceptor implements HandlerInterceptor {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final String CORRELATION_ID_KEY = "correlationId";

    /**
     * Pre-handle: Extract or generate correlation ID, add to MDC.
     * Skips processing for OPTIONS requests (CORS preflight).
     */
    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler) {

        // Skip processing for OPTIONS requests (CORS preflight)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // Extract or generate correlation ID
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
            log.debug("Generated new correlation ID: {}", correlationId);
        } else {
            log.debug("Using correlation ID from header: {}", correlationId);
        }

        // Add to MDC for logging
        MDC.put(CORRELATION_ID_KEY, correlationId);

        // Add to response header
        response.setHeader(CORRELATION_ID_HEADER, correlationId);

        return true;
    }

    /**
     * After completion: Clean up MDC.
     */
    @Override
    public void afterCompletion(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler,
            Exception ex) {

        // Remove from MDC to prevent memory leak
        MDC.remove(CORRELATION_ID_KEY);
    }
}
