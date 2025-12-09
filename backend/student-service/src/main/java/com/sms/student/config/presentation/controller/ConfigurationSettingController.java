package com.sms.student.config.presentation.controller;

import com.sms.student.config.application.service.ConfigurationSettingService;
import com.sms.student.config.presentation.dto.ConfigurationSettingDTO;
import com.sms.student.config.presentation.dto.ConfigurationSettingRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for configuration settings management.
 * Provides endpoints for managing application configuration.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/config/settings")
@RequiredArgsConstructor
@Tag(name = "Configuration Settings", description = "APIs for managing system configuration")
public class ConfigurationSettingController {

    private final ConfigurationSettingService configService;

    /**
     * Create a new configuration setting.
     * POST /api/v1/config/settings
     *
     * @param request the configuration setting creation request
     * @return created configuration setting DTO with HTTP 201
     */
    @PostMapping
    @Operation(summary = "Create a new configuration setting",
        description = "Create a new configuration setting with category, key, and value")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Configuration setting created successfully",
            content = @Content(schema = @Schema(implementation = ConfigurationSettingDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "409", description = "Configuration setting already exists")
    })
    public ResponseEntity<ConfigurationSettingDTO> createSetting(
        @Valid @RequestBody ConfigurationSettingRequest request) {
        log.info("POST /api/v1/config/settings - Creating new configuration setting");
        ConfigurationSettingDTO createdSetting = configService.createSetting(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSetting);
    }

    /**
     * Get configuration setting by ID.
     * GET /api/v1/config/settings/{id}
     *
     * @param id the configuration setting ID
     * @return the configuration setting DTO
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get configuration setting by ID",
        description = "Retrieve configuration setting details by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Configuration setting found",
            content = @Content(schema = @Schema(implementation = ConfigurationSettingDTO.class))),
        @ApiResponse(responseCode = "404", description = "Configuration setting not found")
    })
    public ResponseEntity<ConfigurationSettingDTO> getSetting(@PathVariable Long id) {
        log.info("GET /api/v1/config/settings/{} - Fetching configuration setting", id);
        ConfigurationSettingDTO setting = configService.getSetting(id);
        return ResponseEntity.ok(setting);
    }

    /**
     * Update configuration setting by ID.
     * PUT /api/v1/config/settings/{id}
     *
     * @param id the configuration setting ID
     * @param request the update request
     * @return updated configuration setting DTO
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update configuration setting",
        description = "Update configuration setting details")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Configuration setting updated successfully",
            content = @Content(schema = @Schema(implementation = ConfigurationSettingDTO.class))),
        @ApiResponse(responseCode = "404", description = "Configuration setting not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<ConfigurationSettingDTO> updateSetting(
        @PathVariable Long id,
        @Valid @RequestBody ConfigurationSettingRequest request) {
        log.info("PUT /api/v1/config/settings/{} - Updating configuration setting", id);
        ConfigurationSettingDTO updatedSetting = configService.updateSetting(id, request);
        return ResponseEntity.ok(updatedSetting);
    }

    /**
     * Delete configuration setting by ID.
     * DELETE /api/v1/config/settings/{id}
     *
     * @param id the configuration setting ID
     * @return HTTP 204 No Content
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete configuration setting",
        description = "Remove a configuration setting from the system")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Configuration setting deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Configuration setting not found")
    })
    public ResponseEntity<Void> deleteSetting(@PathVariable Long id) {
        log.info("DELETE /api/v1/config/settings/{} - Deleting configuration setting", id);
        configService.deleteSetting(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get all configuration settings for a category.
     * GET /api/v1/config/settings?category={category}&page={page}&size={size}
     *
     * @param category the configuration category
     * @param page the page number (default 0)
     * @param size the page size (default 20)
     * @return page of configuration settings for the category
     */
    @GetMapping
    @Operation(summary = "Get settings by category",
        description = "Retrieve all configuration settings for a specific category with pagination")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Configuration settings found",
            content = @Content(schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters")
    })
    public ResponseEntity<Page<ConfigurationSettingDTO>> getSettingsByCategory(
        @RequestParam(value = "category", defaultValue = "") String category,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "20") int size) {
        log.info("GET /api/v1/config/settings?category={}&page={}&size={} - Fetching configuration settings",
            category, page, size);

        // Validate pagination parameters
        if (page < 0) {
            page = 0;
        }
        if (size <= 0 || size > 100) {
            size = 20;
        }

        Page<ConfigurationSettingDTO> result = configService.getSettingsByCategory(category, page, size);
        return ResponseEntity.ok(result);
    }
}
