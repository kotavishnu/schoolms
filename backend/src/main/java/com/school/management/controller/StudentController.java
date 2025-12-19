package com.school.management.controller;

import com.school.management.dto.StudentDTO;
import com.school.management.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    public ResponseEntity<StudentDTO> createStudent(@Valid @RequestBody StudentDTO studentDTO) {
        StudentDTO created = studentService.createStudent(studentDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable Long id) {
        StudentDTO student = studentService.getStudentById(id);
        return ResponseEntity.ok(student);
    }

    @GetMapping
    public ResponseEntity<List<StudentDTO>> getAllStudents(
            @RequestParam(required = false) Long classId) {

        if (classId != null) {
            List<StudentDTO> students = studentService.getStudentsByClassId(classId);
            return ResponseEntity.ok(students);
        }

        List<StudentDTO> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/search")
    public ResponseEntity<List<StudentDTO>> searchStudents(@RequestParam String query) {
        List<StudentDTO> students = studentService.searchStudents(query);
        return ResponseEntity.ok(students);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentDTO> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentDTO studentDTO) {
        StudentDTO updated = studentService.updateStudent(id, studentDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}
