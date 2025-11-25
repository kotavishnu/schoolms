package com.school.sms.configuration.presentation.controller;

import com.school.sms.configuration.application.dto.request.CreateConfigRequest;
import com.school.sms.configuration.application.dto.request.UpdateConfigRequest;
import com.school.sms.configuration.application.dto.response.ConfigurationPageResponse;
import com.school.sms.configuration.application.dto.response.ConfigurationResponse;
import com.school.sms.configuration.application.service.ConfigurationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/configurations")
@RequiredArgsConstructor
@Tag(name = "Configuration Management", description = "APIs for managing school configuration settings")
public class ConfigurationController {

    private final ConfigurationService configurationService;

    @PostMapping
    @Operation(summary = "Create new configuration", description = "Creates a new configuration setting with category and key-value pair")
    public ResponseEntity<ConfigurationResponse> createConfiguration(
            @Valid @RequestBody CreateConfigRequest request) {
        log.info("POST /api/v1/configurations - Creating configuration: category={}, key={}",
                request.getCategory(), request.getConfigKey());

        ConfigurationResponse response = configurationService.createConfiguration(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "List all configurations", description = "Retrieve paginated list of all configurations")
    public ResponseEntity<ConfigurationPageResponse> listConfigurations(Pageable pageable) {
        log.info("GET /api/v1/configurations - Listing all configurations");

        ConfigurationPageResponse response = configurationService.getAllConfigurations(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get configurations by category", description = "Retrieve all configurations for a specific category")
    public ResponseEntity<List<ConfigurationResponse>> getByCategory(@PathVariable String category) {
        log.info("GET /api/v1/configurations/category/{} - Fetching configurations", category);

        List<ConfigurationResponse> response = configurationService.getConfigurationsByCategory(category);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get configuration by ID", description = "Retrieve configuration details by ID")
    public ResponseEntity<ConfigurationResponse> getConfigurationById(@PathVariable Long id) {
        log.info("GET /api/v1/configurations/{} - Fetching configuration", id);

        ConfigurationResponse response = configurationService.getConfigurationById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update configuration", description = "Update configuration value and description")
    public ResponseEntity<ConfigurationResponse> updateConfiguration(
            @PathVariable Long id,
            @Valid @RequestBody UpdateConfigRequest request) {
        log.info("PUT /api/v1/configurations/{} - Updating configuration", id);

        ConfigurationResponse response = configurationService.updateConfiguration(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete configuration", description = "Delete configuration by ID")
    public ResponseEntity<Void> deleteConfiguration(@PathVariable Long id) {
        log.info("DELETE /api/v1/configurations/{} - Deleting configuration", id);

        configurationService.deleteConfiguration(id);
        return ResponseEntity.noContent().build();
    }
}
