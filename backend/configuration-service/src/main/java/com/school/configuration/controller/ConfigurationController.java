package com.school.configuration.controller;

import com.school.configuration.controller.dto.request.ConfigurationRequest;
import com.school.configuration.controller.dto.response.ConfigurationResponse;
import com.school.configuration.domain.model.ConfigCategory;
import com.school.configuration.service.ConfigurationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST API controller for configuration management.
 * Provides endpoints for CRUD operations on system configurations.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/configurations")
@RequiredArgsConstructor
@Tag(name = "Configuration Management", description = "APIs for managing system configurations")
public class ConfigurationController {

    private final ConfigurationService configurationService;

    /**
     * Get all configurations, optionally filtered by category.
     *
     * @param category Optional category filter (GENERAL, ACADEMIC, FINANCIAL)
     * @return List of configurations
     */
    @GetMapping
    @Operation(summary = "Get all configurations", description = "Retrieve all configurations, optionally filtered by category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configurations retrieved successfully")
    })
    public ResponseEntity<List<ConfigurationResponse>> getAllConfigurations(
            @Parameter(description = "Configuration category filter (optional)")
            @RequestParam(required = false) ConfigCategory category) {
        log.debug("GET /api/v1/configurations - category: {}", category);

        List<ConfigurationResponse> configurations = configurationService.getAllConfigurations(category);
        return ResponseEntity.ok(configurations);
    }

    /**
     * Get a single configuration by category and key.
     *
     * @param category Configuration category
     * @param key Configuration key
     * @return Configuration response
     */
    @GetMapping("/{category}/{key}")
    @Operation(summary = "Get configuration by category and key", description = "Retrieve a specific configuration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configuration found"),
            @ApiResponse(responseCode = "404", description = "Configuration not found")
    })
    public ResponseEntity<ConfigurationResponse> getConfiguration(
            @Parameter(description = "Configuration category") @PathVariable ConfigCategory category,
            @Parameter(description = "Configuration key") @PathVariable String key) {
        log.debug("GET /api/v1/configurations/{}/{}", category, key);

        ConfigurationResponse response = configurationService.getConfiguration(category, key);
        return ResponseEntity.ok(response);
    }

    /**
     * Create or update a configuration (upsert operation).
     * Returns 201 Created if a new configuration was created, 200 OK if updated.
     *
     * @param category Configuration category
     * @param key Configuration key
     * @param request Configuration request data
     * @return Configuration response
     */
    @PutMapping("/{category}/{key}")
    @Operation(summary = "Create or update configuration", description = "Upsert a configuration (create if not exists, update if exists)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configuration updated successfully"),
            @ApiResponse(responseCode = "201", description = "Configuration created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<ConfigurationResponse> upsertConfiguration(
            @Parameter(description = "Configuration category") @PathVariable ConfigCategory category,
            @Parameter(description = "Configuration key") @PathVariable String key,
            @Valid @RequestBody ConfigurationRequest request) {
        log.debug("PUT /api/v1/configurations/{}/{} - request: {}", category, key, request);

        ConfigurationService.UpsertResult result = configurationService.upsertConfiguration(category, key, request);

        // Return 201 Created if new, 200 OK if updated
        HttpStatus status = result.isCreated() ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(status).body(result.response());
    }

    /**
     * Delete a configuration by category and key.
     *
     * @param category Configuration category
     * @param key Configuration key
     * @return No content
     */
    @DeleteMapping("/{category}/{key}")
    @Operation(summary = "Delete configuration", description = "Delete a configuration by category and key")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Configuration deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Configuration not found")
    })
    public ResponseEntity<Void> deleteConfiguration(
            @Parameter(description = "Configuration category") @PathVariable ConfigCategory category,
            @Parameter(description = "Configuration key") @PathVariable String key) {
        log.debug("DELETE /api/v1/configurations/{}/{}", category, key);

        configurationService.deleteConfiguration(category, key);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get configurations grouped as a key-value map.
     * Useful for frontend applications that need all configurations in a flat structure.
     *
     * @param category Configuration category
     * @return Map of configuration keys to values
     */
    @GetMapping("/grouped/{category}")
    @Operation(summary = "Get grouped configurations", description = "Retrieve configurations as a key-value map for a category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Grouped configurations retrieved successfully")
    })
    public ResponseEntity<Map<String, String>> getGroupedConfigurations(
            @Parameter(description = "Configuration category") @PathVariable ConfigCategory category) {
        log.debug("GET /api/v1/configurations/grouped/{}", category);

        Map<String, String> grouped = configurationService.getGroupedConfigurations(category);
        return ResponseEntity.ok(grouped);
    }
}
