package com.school.management.controller;

import com.school.management.dto.SchoolClassDTO;
import com.school.management.service.SchoolClassService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class SchoolClassController {

    private final SchoolClassService classService;

    @PostMapping
    public ResponseEntity<SchoolClassDTO> createClass(@Valid @RequestBody SchoolClassDTO classDTO) {
        SchoolClassDTO created = classService.createClass(classDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SchoolClassDTO> getClassById(@PathVariable Long id) {
        SchoolClassDTO schoolClass = classService.getClassById(id);
        return ResponseEntity.ok(schoolClass);
    }

    @GetMapping
    public ResponseEntity<List<SchoolClassDTO>> getAllClasses(
            @RequestParam(required = false) String academicYear) {

        if (academicYear != null) {
            List<SchoolClassDTO> classes = classService.getClassesByAcademicYear(academicYear);
            return ResponseEntity.ok(classes);
        }

        List<SchoolClassDTO> classes = classService.getAllClasses();
        return ResponseEntity.ok(classes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SchoolClassDTO> updateClass(
            @PathVariable Long id,
            @Valid @RequestBody SchoolClassDTO classDTO) {
        SchoolClassDTO updated = classService.updateClass(id, classDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClass(@PathVariable Long id) {
        classService.deleteClass(id);
        return ResponseEntity.noContent().build();
    }
}
