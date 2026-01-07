package com.school.management.controller;

import com.school.management.dto.SchoolConfigDTO;
import com.school.management.service.SchoolConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/school-config")
@RequiredArgsConstructor
public class SchoolConfigController {

    private final SchoolConfigService schoolConfigService;

    @PostMapping
    public ResponseEntity<SchoolConfigDTO> createOrUpdateConfig(
            @Valid @RequestBody SchoolConfigDTO configDTO) {
        SchoolConfigDTO saved = schoolConfigService.createOrUpdateConfig(configDTO);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<SchoolConfigDTO> getConfig() {
        SchoolConfigDTO config = schoolConfigService.getConfig();
        return ResponseEntity.ok(config);
    }
}
