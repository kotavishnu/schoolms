package com.school.sms.configuration.presentation.controller;

import com.school.sms.configuration.application.dto.*;
import com.school.sms.configuration.application.service.ConfigurationManagementService;
import com.school.sms.configuration.domain.enums.SettingCategory;
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
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for configuration settings management.
 */
@RestController
@RequestMapping("/api/v1/configurations/settings")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Configuration Settings", description = "Configuration settings management API")
public class ConfigurationController {

    private final ConfigurationManagementService service;

    private static final String USER_ID_HEADER = "X-User-ID";
    private static final String DEFAULT_USER_ID = "system";

    /**
     * Create a new configuration setting.
     */
    @PostMapping
    @Operation(summary = "Create a new configuration setting",
               description = "Creates a new configuration setting with the specified category and key")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Setting created successfully",
                    content = @Content(schema = @Schema(implementation = SettingResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "409", description = "Setting already exists",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<SettingResponse> createSetting(
            @Valid @RequestBody CreateSettingRequest request,
            @RequestHeader(value = USER_ID_HEADER, defaultValue = DEFAULT_USER_ID) String userId) {

        log.info("Received request to create setting: {}.{}", request.getCategory(), request.getKey());

        SettingResponse response = service.createSetting(request, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get configuration setting by ID.
     */
    @GetMapping("/{settingId}")
    @Operation(summary = "Get setting by ID",
               description = "Retrieves a configuration setting by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Setting found",
                    content = @Content(schema = @Schema(implementation = SettingResponse.class))),
        @ApiResponse(responseCode = "404", description = "Setting not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<SettingResponse> getSettingById(
            @Parameter(description = "Setting ID", required = true)
            @PathVariable Long settingId) {

        log.debug("Received request to get setting by ID: {}", settingId);

        SettingResponse response = service.getSettingById(settingId);

        return ResponseEntity.ok(response);
    }

    /**
     * Update a configuration setting.
     */
    @PutMapping("/{settingId}")
    @Operation(summary = "Update a configuration setting",
               description = "Updates the value and description of an existing configuration setting")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Setting updated successfully",
                    content = @Content(schema = @Schema(implementation = SettingResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "404", description = "Setting not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "409", description = "Optimistic locking failure",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<SettingResponse> updateSetting(
            @Parameter(description = "Setting ID", required = true)
            @PathVariable Long settingId,
            @Valid @RequestBody UpdateSettingRequest request,
            @RequestHeader(value = USER_ID_HEADER, defaultValue = DEFAULT_USER_ID) String userId) {

        log.info("Received request to update setting: ID={}", settingId);

        SettingResponse response = service.updateSetting(settingId, request, userId);

        return ResponseEntity.ok(response);
    }

    /**
     * Delete a configuration setting.
     */
    @DeleteMapping("/{settingId}")
    @Operation(summary = "Delete a configuration setting",
               description = "Deletes a configuration setting by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Setting deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Setting not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<Void> deleteSetting(
            @Parameter(description = "Setting ID", required = true)
            @PathVariable Long settingId) {

        log.info("Received request to delete setting: ID={}", settingId);

        service.deleteSetting(settingId);

        return ResponseEntity.noContent().build();
    }

    /**
     * Get all settings for a specific category.
     */
    @GetMapping("/category/{category}")
    @Operation(summary = "Get settings by category",
               description = "Retrieves all configuration settings for a specific category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Settings retrieved successfully",
                    content = @Content(schema = @Schema(implementation = CategorySettingsResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid category",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<CategorySettingsResponse> getSettingsByCategory(
            @Parameter(description = "Setting category (GENERAL, ACADEMIC, FINANCIAL)", required = true)
            @PathVariable SettingCategory category) {

        log.debug("Received request to get settings for category: {}", category);

        CategorySettingsResponse response = service.getSettingsByCategory(category);

        return ResponseEntity.ok(response);
    }

    /**
     * Get all settings grouped by category.
     */
    @GetMapping
    @Operation(summary = "Get all settings",
               description = "Retrieves all configuration settings grouped by category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Settings retrieved successfully")
    })
    public ResponseEntity<Map<SettingCategory, List<SettingResponse>>> getAllSettingsGroupedByCategory() {
        log.debug("Received request to get all settings grouped by category");

        Map<SettingCategory, List<SettingResponse>> response = service.getAllSettingsGroupedByCategory();

        return ResponseEntity.ok(response);
    }
}
