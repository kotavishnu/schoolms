package com.school.management.controller;

import com.school.management.dto.request.SchoolConfigRequestDTO;
import com.school.management.dto.response.ApiResponse;
import com.school.management.dto.response.SchoolConfigResponseDTO;
import com.school.management.service.SchoolConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for School Configuration management operations.
 *
 * Provides endpoints for CRUD operations, category filtering, and configuration management.
 *
 * @author School Management Team
 */
@RestController
@RequestMapping("/api/school-config")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class SchoolConfigController {

    private final SchoolConfigService schoolConfigService;

    /**
     * Create a new school configuration
     *
     * POST /api/school-config
     *
     * @param requestDTO Configuration data
     * @return Created configuration with HTTP 201
     */
    @PostMapping
    public ResponseEntity<ApiResponse<SchoolConfigResponseDTO>> createConfig(
            @Valid @RequestBody SchoolConfigRequestDTO requestDTO) {

        log.info("POST /api/school-config - Creating config: {}", requestDTO.getConfigKey());

        SchoolConfigResponseDTO config = schoolConfigService.createConfig(requestDTO);

        ApiResponse<SchoolConfigResponseDTO> response = ApiResponse.success(
                "Configuration created successfully", config);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get configuration by ID
     *
     * GET /api/school-config/{id}
     *
     * @param id Configuration ID
     * @return Configuration details
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SchoolConfigResponseDTO>> getConfig(@PathVariable Long id) {
        log.info("GET /api/school-config/{}", id);

        SchoolConfigResponseDTO config = schoolConfigService.getConfig(id);

        ApiResponse<SchoolConfigResponseDTO> response = ApiResponse.success(config);

        return ResponseEntity.ok(response);
    }

    /**
     * Get configuration by key
     *
     * GET /api/school-config/key/{key}
     *
     * @param key Configuration key
     * @return Configuration details
     */
    @GetMapping("/key/{key}")
    public ResponseEntity<ApiResponse<SchoolConfigResponseDTO>> getConfigByKey(@PathVariable String key) {
        log.info("GET /api/school-config/key/{}", key);

        SchoolConfigResponseDTO config = schoolConfigService.getConfigByKey(key);

        ApiResponse<SchoolConfigResponseDTO> response = ApiResponse.success(config);

        return ResponseEntity.ok(response);
    }

    /**
     * Get configuration value by key
     *
     * GET /api/school-config/value/{key}
     *
     * @param key Configuration key
     * @return Configuration value as string
     */
    @GetMapping("/value/{key}")
    public ResponseEntity<ApiResponse<String>> getConfigValue(@PathVariable String key) {
        log.info("GET /api/school-config/value/{}", key);

        String value = schoolConfigService.getConfigValue(key);

        ApiResponse<String> response = ApiResponse.success(value);

        return ResponseEntity.ok(response);
    }

    /**
     * Get all configurations or filter by category
     *
     * GET /api/school-config
     * GET /api/school-config?category=GENERAL
     *
     * @param category Optional category filter
     * @return List of configurations
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<SchoolConfigResponseDTO>>> getAllConfigs(
            @RequestParam(required = false) String category) {

        log.info("GET /api/school-config - Category filter: {}", category);

        List<SchoolConfigResponseDTO> configs = category != null
                ? schoolConfigService.getConfigsByCategory(category)
                : schoolConfigService.getAllConfigs();

        ApiResponse<List<SchoolConfigResponseDTO>> response = ApiResponse.success(configs);

        return ResponseEntity.ok(response);
    }

    /**
     * Get editable configurations only
     *
     * GET /api/school-config/editable
     *
     * @return List of editable configurations
     */
    @GetMapping("/editable")
    public ResponseEntity<ApiResponse<List<SchoolConfigResponseDTO>>> getEditableConfigs() {
        log.info("GET /api/school-config/editable");

        List<SchoolConfigResponseDTO> configs = schoolConfigService.getEditableConfigs();

        ApiResponse<List<SchoolConfigResponseDTO>> response = ApiResponse.success(configs);

        return ResponseEntity.ok(response);
    }

    /**
     * Update configuration
     *
     * PUT /api/school-config/{id}
     *
     * @param id Configuration ID
     * @param requestDTO Updated configuration data
     * @return Updated configuration
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SchoolConfigResponseDTO>> updateConfig(
            @PathVariable Long id,
            @Valid @RequestBody SchoolConfigRequestDTO requestDTO) {

        log.info("PUT /api/school-config/{}", id);

        SchoolConfigResponseDTO config = schoolConfigService.updateConfig(id, requestDTO);

        ApiResponse<SchoolConfigResponseDTO> response = ApiResponse.success(
                "Configuration updated successfully", config);

        return ResponseEntity.ok(response);
    }

    /**
     * Update configuration value only
     *
     * PATCH /api/school-config/{key}
     *
     * @param key Configuration key
     * @param value New value (from request body as plain text or JSON)
     * @return Updated configuration
     */
    @PatchMapping("/{key}")
    public ResponseEntity<ApiResponse<SchoolConfigResponseDTO>> updateConfigValue(
            @PathVariable String key,
            @RequestBody String value) {

        log.info("PATCH /api/school-config/{}", key);

        SchoolConfigResponseDTO config = schoolConfigService.updateConfigValue(key, value);

        ApiResponse<SchoolConfigResponseDTO> response = ApiResponse.success(
                "Configuration value updated successfully", config);

        return ResponseEntity.ok(response);
    }

    /**
     * Delete configuration
     *
     * DELETE /api/school-config/{id}
     *
     * @param id Configuration ID
     * @return Success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteConfig(@PathVariable Long id) {
        log.info("DELETE /api/school-config/{}", id);

        schoolConfigService.deleteConfig(id);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Configuration deleted successfully")
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Check if configuration key exists
     *
     * GET /api/school-config/exists/{key}
     *
     * @param key Configuration key
     * @return Boolean indicating existence
     */
    @GetMapping("/exists/{key}")
    public ResponseEntity<ApiResponse<Boolean>> configExists(@PathVariable String key) {
        log.info("GET /api/school-config/exists/{}", key);

        boolean exists = schoolConfigService.configExists(key);

        ApiResponse<Boolean> response = ApiResponse.success(exists);

        return ResponseEntity.ok(response);
    }
}
