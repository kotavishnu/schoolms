package com.school.sms.configuration.presentation.controller;

import com.school.sms.configuration.application.dto.request.CreateConfigurationRequest;
import com.school.sms.configuration.application.dto.response.ConfigurationResponse;
import com.school.sms.configuration.application.dto.response.GroupedConfigurationResponse;
import com.school.sms.configuration.application.service.ConfigurationApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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

/**
 * REST controller for configuration operations.
 *
 * <p>Base path: /api/v1/configurations</p>
 *
 * <p>This controller provides CRUD operations for configuration settings,
 * including UPSERT functionality (create or update) and grouped retrieval.</p>
 *
 * @author School Management System Team
 * @version 1.0.0
 * @since 2025-12-06
 */
@RestController
@RequestMapping("/api/v1/configurations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Configuration Management", description = "APIs for managing school configuration settings")
public class ConfigurationController {

    private final ConfigurationApplicationService configurationService;

    /**
     * Retrieves all configurations, optionally filtered by category.
     *
     * @param category optional category filter (GENERAL, ACADEMIC, FINANCIAL)
     * @return the list of configurations (200 OK)
     */
    @GetMapping
    @Operation(summary = "Get all configurations", description = "Retrieves all configuration settings, optionally filtered by category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Configurations retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid category")
    })
    public ResponseEntity<List<ConfigurationResponse>> getAllConfigurations(
        @Parameter(description = "Optional category filter (GENERAL, ACADEMIC, FINANCIAL)")
        @RequestParam(required = false) String category
    ) {
        log.info("GET /api/v1/configurations - Retrieving configurations with category filter: {}", category);

        List<ConfigurationResponse> configurations = configurationService.getAllConfigurations(category);

        return ResponseEntity.ok(configurations);
    }

    /**
     * Retrieves a specific configuration by category and key.
     *
     * @param category the configuration category
     * @param key the configuration key
     * @return the configuration response (200 OK)
     */
    @GetMapping("/{category}/{key}")
    @Operation(summary = "Get configuration by category and key", description = "Retrieves a specific configuration setting")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Configuration found",
                     content = @Content(schema = @Schema(implementation = ConfigurationResponse.class))),
        @ApiResponse(responseCode = "404", description = "Configuration not found"),
        @ApiResponse(responseCode = "400", description = "Invalid category")
    })
    public ResponseEntity<ConfigurationResponse> getConfiguration(
        @Parameter(description = "Category (GENERAL, ACADEMIC, FINANCIAL)", example = "GENERAL")
        @PathVariable String category,
        @Parameter(description = "Configuration key", example = "school_name")
        @PathVariable String key
    ) {
        log.info("GET /api/v1/configurations/{}/{} - Retrieving configuration", category, key);

        ConfigurationResponse response = configurationService.getConfiguration(category, key);

        return ResponseEntity.ok(response);
    }

    /**
     * Creates or updates a configuration setting (UPSERT operation).
     *
     * <p>If a configuration with the same category and key exists, it will be updated.
     * Otherwise, a new configuration will be created.</p>
     *
     * @param category the configuration category
     * @param key the configuration key
     * @param request the create/update configuration request
     * @return the saved configuration response (200 OK)
     */
    @PutMapping("/{category}/{key}")
    @Operation(summary = "Create or update configuration", description = "Creates a new configuration or updates an existing one (UPSERT)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Configuration saved successfully",
                     content = @Content(schema = @Schema(implementation = ConfigurationResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input or category"),
        @ApiResponse(responseCode = "409", description = "Version conflict")
    })
    public ResponseEntity<ConfigurationResponse> createOrUpdateConfiguration(
        @Parameter(description = "Category (GENERAL, ACADEMIC, FINANCIAL)", example = "GENERAL")
        @PathVariable String category,
        @Parameter(description = "Configuration key", example = "school_name")
        @PathVariable String key,
        @Valid @RequestBody CreateConfigurationRequest request
    ) {
        log.info("PUT /api/v1/configurations/{}/{} - Creating or updating configuration", category, key);

        ConfigurationResponse response = configurationService.createOrUpdate(category, key, request);

        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a configuration setting.
     *
     * @param category the configuration category
     * @param key the configuration key
     * @return no content (204 No Content)
     */
    @DeleteMapping("/{category}/{key}")
    @Operation(summary = "Delete configuration", description = "Deletes a configuration setting")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Configuration deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Configuration not found"),
        @ApiResponse(responseCode = "400", description = "Invalid category")
    })
    public ResponseEntity<Void> deleteConfiguration(
        @Parameter(description = "Category (GENERAL, ACADEMIC, FINANCIAL)", example = "GENERAL")
        @PathVariable String category,
        @Parameter(description = "Configuration key", example = "school_name")
        @PathVariable String key
    ) {
        log.info("DELETE /api/v1/configurations/{}/{} - Deleting configuration", category, key);

        configurationService.deleteConfiguration(category, key);

        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves configurations grouped by category as a simple key-value map.
     *
     * <p>This endpoint returns a Map<String, String> for easy consumption
     * by frontend applications.</p>
     *
     * @param category the configuration category
     * @return the grouped configuration response (200 OK)
     */
    @GetMapping("/grouped/{category}")
    @Operation(summary = "Get configurations grouped by category", description = "Retrieves all configurations in a category as a simple key-value map")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Configurations retrieved successfully",
                     content = @Content(schema = @Schema(implementation = GroupedConfigurationResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid category")
    })
    public ResponseEntity<GroupedConfigurationResponse> getGroupedConfigurations(
        @Parameter(description = "Category (GENERAL, ACADEMIC, FINANCIAL)", example = "GENERAL")
        @PathVariable String category
    ) {
        log.info("GET /api/v1/configurations/grouped/{} - Retrieving grouped configurations", category);

        GroupedConfigurationResponse response = configurationService.getGroupedByCategory(category);

        return ResponseEntity.ok(response);
    }
}
