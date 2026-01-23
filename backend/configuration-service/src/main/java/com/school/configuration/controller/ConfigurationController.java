package com.school.configuration.controller;

import com.school.configuration.domain.entity.ConfigCategory;
import com.school.configuration.dto.request.ConfigurationRequest;
import com.school.configuration.dto.response.ConfigurationResponse;
import com.school.configuration.service.ConfigurationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Configuration Controller
 *
 * REST API endpoints for configuration management.
 * Base path: /api/v1/configurations
 *
 * All endpoints return RFC 7807 Problem Details for errors.
 *
 * Endpoints:
 * 1. GET /api/v1/configurations - Get all configurations
 * 2. GET /api/v1/configurations?category={category} - Get by category
 * 3. GET /api/v1/configurations/{category}/{key} - Get specific configuration
 * 4. PUT /api/v1/configurations/{category}/{key} - Upsert configuration
 * 5. DELETE /api/v1/configurations/{category}/{key} - Delete configuration
 * 6. GET /api/v1/configurations/grouped/{category} - Get grouped configurations (map)
 */
@RestController
@RequestMapping("/api/v1/configurations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Configuration Management", description = "APIs for system configuration settings")
public class ConfigurationController {

    private final ConfigurationService configurationService;

    /**
     * Get all configurations or filter by category
     *
     * GET /api/v1/configurations
     * GET /api/v1/configurations?category=GENERAL
     *
     * @param category Optional category filter
     * @return List of configurations
     */
    @GetMapping
    @Operation(
        summary = "Get all configurations",
        description = "Get all configurations or filter by category (GENERAL, ACADEMIC, FINANCIAL, SYSTEM)"
    )
    public ResponseEntity<List<ConfigurationResponse>> getConfigurations(
        @RequestParam(required = false) ConfigCategory category
    ) {
        if (category != null) {
            log.debug("GET /api/v1/configurations?category={}", category);
            List<ConfigurationResponse> configurations = configurationService.getConfigurationsByCategory(category);
            return ResponseEntity.ok(configurations);
        } else {
            log.debug("GET /api/v1/configurations (all)");
            List<ConfigurationResponse> configurations = configurationService.getAllConfigurations();
            return ResponseEntity.ok(configurations);
        }
    }

    /**
     * Get specific configuration by category and key
     *
     * GET /api/v1/configurations/{category}/{key}
     *
     * Example: GET /api/v1/configurations/GENERAL/SCHOOL_NAME
     *
     * @param category Configuration category
     * @param key Configuration key
     * @return Configuration details
     * @throws com.school.configuration.exception.ConfigurationNotFoundException if not found (404)
     */
    @GetMapping("/{category}/{key}")
    @Operation(
        summary = "Get configuration by category and key",
        description = "Retrieve a specific configuration setting"
    )
    public ResponseEntity<ConfigurationResponse> getConfiguration(
        @PathVariable ConfigCategory category,
        @PathVariable String key
    ) {
        log.debug("GET /api/v1/configurations/{}/{}", category, key);

        ConfigurationResponse configuration = configurationService.getConfiguration(category, key);

        return ResponseEntity.ok(configuration);
    }

    /**
     * Upsert configuration (create if not exists, update if exists)
     *
     * PUT /api/v1/configurations/{category}/{key}
     *
     * Example: PUT /api/v1/configurations/GENERAL/SCHOOL_NAME
     *
     * Request body:
     * {
     *   "category": "GENERAL",
     *   "key": "SCHOOL_NAME",
     *   "value": "ABC School",
     *   "description": "Official school name",
     *   "dataType": "STRING",
     *   "isEncrypted": false,
     *   "version": 0
     * }
     *
     * @param category Configuration category (path variable)
     * @param key Configuration key (path variable)
     * @param request Configuration data (must match path variables)
     * @return Created or updated configuration
     * @throws jakarta.persistence.OptimisticLockException if version conflict (409)
     */
    @PutMapping("/{category}/{key}")
    @Operation(
        summary = "Upsert configuration",
        description = "Create new configuration or update existing one (upsert operation)"
    )
    public ResponseEntity<ConfigurationResponse> upsertConfiguration(
        @PathVariable ConfigCategory category,
        @PathVariable String key,
        @Valid @RequestBody ConfigurationRequest request
    ) {
        log.info("PUT /api/v1/configurations/{}/{}", category, key);

        // Ensure path variables match request body
        if (!category.equals(request.getCategory()) || !key.equals(request.getKey())) {
            log.warn("Path variables do not match request body: path={}:{}, body={}:{}",
                category, key, request.getCategory(), request.getKey());
            throw new IllegalArgumentException(
                "Category and key in path must match request body"
            );
        }

        ConfigurationResponse configuration = configurationService.upsertConfiguration(request);

        return ResponseEntity.ok(configuration);
    }

    /**
     * Delete configuration
     *
     * DELETE /api/v1/configurations/{category}/{key}
     *
     * Example: DELETE /api/v1/configurations/GENERAL/TEMP_SETTING
     *
     * @param category Configuration category
     * @param key Configuration key
     * @return 204 No Content
     * @throws com.school.configuration.exception.ConfigurationNotFoundException if not found (404)
     */
    @DeleteMapping("/{category}/{key}")
    @Operation(
        summary = "Delete configuration",
        description = "Delete a configuration setting"
    )
    public ResponseEntity<Void> deleteConfiguration(
        @PathVariable ConfigCategory category,
        @PathVariable String key
    ) {
        log.info("DELETE /api/v1/configurations/{}/{}", category, key);

        configurationService.deleteConfiguration(category, key);

        return ResponseEntity.noContent().build();
    }

    /**
     * Get grouped configurations as key-value map
     *
     * GET /api/v1/configurations/grouped/{category}
     *
     * Example: GET /api/v1/configurations/grouped/GENERAL
     *
     * Returns:
     * {
     *   "SCHOOL_NAME": "ABC School",
     *   "SCHOOL_ADDRESS": "123 Main St",
     *   "CONTACT_EMAIL": "contact@school.com"
     * }
     *
     * @param category Configuration category
     * @return Map of key → value
     */
    @GetMapping("/grouped/{category}")
    @Operation(
        summary = "Get grouped configurations",
        description = "Get all configurations in a category as a key-value map"
    )
    public ResponseEntity<Map<String, String>> getGroupedConfigurations(
        @PathVariable ConfigCategory category
    ) {
        log.debug("GET /api/v1/configurations/grouped/{}", category);

        Map<String, String> groupedConfig = configurationService.getGroupedConfigurations(category);

        return ResponseEntity.ok(groupedConfig);
    }
}
