package com.school.management.controller;

import com.school.management.dto.FeeMasterDTO;
import com.school.management.service.FeeMasterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fee-masters")
@RequiredArgsConstructor
public class FeeMasterController {

    private final FeeMasterService feeMasterService;

    @PostMapping
    public ResponseEntity<FeeMasterDTO> createFeeMaster(@Valid @RequestBody FeeMasterDTO feeMasterDTO) {
        FeeMasterDTO created = feeMasterService.createFeeMaster(feeMasterDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeeMasterDTO> getFeeMasterById(@PathVariable Long id) {
        FeeMasterDTO feeMaster = feeMasterService.getFeeMasterById(id);
        return ResponseEntity.ok(feeMaster);
    }

    @GetMapping
    public ResponseEntity<List<FeeMasterDTO>> getAllFeeMasters(
            @RequestParam(required = false) String academicYear,
            @RequestParam(required = false) Integer classNumber) {

        if (academicYear != null && classNumber != null) {
            List<FeeMasterDTO> fees = feeMasterService.getApplicableFeesForClass(academicYear, classNumber);
            return ResponseEntity.ok(fees);
        }

        if (academicYear != null) {
            List<FeeMasterDTO> fees = feeMasterService.getFeeMastersByAcademicYear(academicYear);
            return ResponseEntity.ok(fees);
        }

        List<FeeMasterDTO> fees = feeMasterService.getAllFeeMasters();
        return ResponseEntity.ok(fees);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FeeMasterDTO> updateFeeMaster(
            @PathVariable Long id,
            @Valid @RequestBody FeeMasterDTO feeMasterDTO) {
        FeeMasterDTO updated = feeMasterService.updateFeeMaster(id, feeMasterDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeeMaster(@PathVariable Long id) {
        feeMasterService.deleteFeeMaster(id);
        return ResponseEntity.noContent().build();
    }
}
