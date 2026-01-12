package com.schoolms.configuration.presentation.controller;

import com.schoolms.configuration.application.service.ConfigurationApplicationService;
import com.schoolms.configuration.domain.model.ConfigCategory;
import com.schoolms.configuration.presentation.dto.ConfigurationListResponse;
import com.schoolms.configuration.presentation.dto.ConfigurationResponse;
import com.schoolms.configuration.presentation.dto.CreateConfigurationRequest;
import com.schoolms.configuration.presentation.dto.UpdateConfigurationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;

/**
 * Configuration Controller (BE-038)
 * REST API for configurations
 */
@RestController
@RequestMapping("/api/v1/configurations")
@Tag(name = "Configurations", description = "Configuration management APIs")
public class ConfigurationController {

    private final ConfigurationApplicationService configurationApplicationService;

    public ConfigurationController(ConfigurationApplicationService configurationApplicationService) {
        this.configurationApplicationService = configurationApplicationService;
    }

    /**
     * GET /api/v1/configurations - List all configurations
     */
    @GetMapping
    @Operation(summary = "List all configurations", description = "Returns a list of configurations with optional category filter")
    public ResponseEntity<ConfigurationListResponse> listConfigurations(
        @RequestParam(required = false) ConfigCategory category
    ) {
        ConfigurationListResponse response = configurationApplicationService.listConfigurations(category);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/configurations/{id} - Get configuration by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get configuration by ID", description = "Returns a configuration by ID")
    public ResponseEntity<ConfigurationResponse> getConfiguration(@PathVariable Long id) {
        ConfigurationResponse response = configurationApplicationService.getConfiguration(id);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/configurations - Create configuration
     */
    @PostMapping
    @Operation(summary = "Create configuration", description = "Creates a new configuration")
    public ResponseEntity<ConfigurationResponse> createConfiguration(
        @Valid @RequestBody CreateConfigurationRequest request
    ) {
        ConfigurationResponse response = configurationApplicationService.createConfiguration(request);

        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(response.id())
            .toUri();

        return ResponseEntity.created(location).body(response);
    }

    /**
     * PATCH /api/v1/configurations/{id} - Update configuration
     */
    @PatchMapping("/{id}")
    @Operation(summary = "Update configuration", description = "Updates an existing configuration")
    public ResponseEntity<ConfigurationResponse> updateConfiguration(
        @PathVariable Long id,
        @Valid @RequestBody UpdateConfigurationRequest request
    ) {
        ConfigurationResponse response = configurationApplicationService.updateConfiguration(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/v1/configurations/{id} - Delete configuration
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete configuration", description = "Deletes a configuration")
    public ResponseEntity<Void> deleteConfiguration(@PathVariable Long id) {
        configurationApplicationService.deleteConfiguration(id);
        return ResponseEntity.noContent().build();
    }
}
