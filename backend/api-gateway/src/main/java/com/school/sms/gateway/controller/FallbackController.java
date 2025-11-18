package com.school.sms.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Fallback controller for handling service unavailability.
 * Provides user-friendly error messages when downstream services are down.
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/student-service")
    public ResponseEntity<Map<String, Object>> studentServiceFallback() {
        return createFallbackResponse("Student Service is temporarily unavailable. Please try again later.");
    }

    @GetMapping("/configuration-service")
    public ResponseEntity<Map<String, Object>> configurationServiceFallback() {
        return createFallbackResponse("Configuration Service is temporarily unavailable. Please try again later.");
    }

    private ResponseEntity<Map<String, Object>> createFallbackResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("type", "https://api.school.com/problems/service-unavailable");
        response.put("title", "Service Unavailable");
        response.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        response.put("detail", message);
        response.put("timestamp", LocalDateTime.now().toString());

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
}
