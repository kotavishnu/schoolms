package com.school.sms.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Global filter for logging all incoming requests through the API Gateway.
 * Adds request ID for distributed tracing.
 */
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);
    private static final String REQUEST_ID_HEADER = "X-Request-ID";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Generate or retrieve request ID
        String requestId = request.getHeaders().getFirst(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }

        // Log request details
        logger.info("Incoming request - Method: {}, Path: {}, Request-ID: {}",
            request.getMethod(),
            request.getPath(),
            requestId);

        // Add request ID to exchange attributes for downstream services
        exchange.getAttributes().put(REQUEST_ID_HEADER, requestId);

        // Continue filter chain and log response
        long startTime = System.currentTimeMillis();
        return chain.filter(exchange).doFinally(signalType -> {
            long duration = System.currentTimeMillis() - startTime;
            logger.info("Request completed - Request-ID: {}, Status: {}, Duration: {}ms",
                requestId,
                exchange.getResponse().getStatusCode(),
                duration);
        });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
