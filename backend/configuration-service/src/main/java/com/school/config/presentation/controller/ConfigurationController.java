package com.school.config.presentation.controller;

import com.school.config.application.service.ConfigurationService;
import com.school.config.presentation.dto.ConfigurationRequestDTO;
import com.school.config.presentation.dto.ConfigurationResponseDTO;
import com.school.config.presentation.dto.ConfigurationUpdateRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Map;

/**
 * REST controller for configuration management operations.
 *
 * @author Backend Developer Agent
 * @version 1.0.0
 * @since 2026-02-04
 */
@RestController
@RequestMapping("/api/v1/configurations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Configurations", description = "Configuration management operations")
@CrossOrigin(
    origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:5173"},
    allowedHeaders = "*",
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
    allowCredentials = "true"
)
public class ConfigurationController {

    private final ConfigurationService configurationService;

    @PostMapping
    @Operation(summary = "Create a new configuration", description = "Creates a new configuration setting")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Configuration created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or duplicate configuration"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ConfigurationResponseDTO> createConfiguration(
        @Valid @RequestBody ConfigurationRequestDTO requestDTO
    ) {
        log.info("POST /api/v1/configurations - Creating configuration");

        ConfigurationResponseDTO response = configurationService.createConfiguration(requestDTO);

        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(response.id())
            .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get configuration by ID", description = "Retrieves a configuration by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Configuration found"),
        @ApiResponse(responseCode = "404", description = "Configuration not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ConfigurationResponseDTO> getConfigurationById(
        @Parameter(description = "Configuration ID", required = true)
        @PathVariable Long id
    ) {
        log.info("GET /api/v1/configurations/{} - Fetching configuration", id);

        ConfigurationResponseDTO response = configurationService.getConfigurationById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/grouped/{category}")
    @Operation(summary = "Get grouped settings by category",
        description = "Retrieves all configuration settings for a category as key-value pairs. Cached for 5 minutes.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Settings retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid category"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Map<String, String>> getGroupedSettings(
        @Parameter(description = "Configuration category (GENERAL, ACADEMIC, FINANCIAL)", required = true)
        @PathVariable String category
    ) {
        log.info("GET /api/v1/configurations/grouped/{} - Fetching grouped settings", category);

        Map<String, String> settings = configurationService.getGroupedSettings(category.toUpperCase());

        return ResponseEntity.ok(settings);
    }

    @GetMapping
    @Operation(summary = "Get all configurations", description = "Retrieves all configurations with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Configurations retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<ConfigurationResponseDTO>> getAllConfigurations(
        @Parameter(description = "Page number (0-based)")
        @RequestParam(defaultValue = "0") int page,

        @Parameter(description = "Page size (max 100)")
        @RequestParam(defaultValue = "20") int size,

        @Parameter(description = "Sort by field")
        @RequestParam(defaultValue = "id") String sortBy,

        @Parameter(description = "Sort direction (ASC or DESC)")
        @RequestParam(defaultValue = "ASC") String sortDirection
    ) {
        log.info("GET /api/v1/configurations - Fetching all configurations");

        // Limit page size to prevent abuse
        size = Math.min(size, 100);

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<ConfigurationResponseDTO> response = configurationService.getAllConfigurations(pageable);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update configuration", description = "Updates an existing configuration's value and description")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Configuration updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Configuration not found"),
        @ApiResponse(responseCode = "409", description = "Optimistic locking conflict"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ConfigurationResponseDTO> updateConfiguration(
        @Parameter(description = "Configuration ID", required = true)
        @PathVariable Long id,

        @Valid @RequestBody ConfigurationUpdateRequestDTO updateDTO
    ) {
        log.info("PUT /api/v1/configurations/{} - Updating configuration", id);

        ConfigurationResponseDTO response = configurationService.updateConfiguration(id, updateDTO);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete configuration", description = "Deletes a configuration by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Configuration deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Configuration not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteConfiguration(
        @Parameter(description = "Configuration ID", required = true)
        @PathVariable Long id
    ) {
        log.info("DELETE /api/v1/configurations/{} - Deleting configuration", id);

        configurationService.deleteConfiguration(id);

        return ResponseEntity.noContent().build();
    }
}
