package com.school.management.controller;

import com.school.management.dto.FeeJournalDTO;
import com.school.management.service.FeeJournalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fee-journals")
@RequiredArgsConstructor
public class FeeJournalController {

    private final FeeJournalService feeJournalService;

    @GetMapping("/{id}")
    public ResponseEntity<FeeJournalDTO> getJournalById(@PathVariable Long id) {
        FeeJournalDTO journal = feeJournalService.getJournalById(id);
        return ResponseEntity.ok(journal);
    }

    @GetMapping
    public ResponseEntity<List<FeeJournalDTO>> getAllJournals(
            @RequestParam(required = false) String academicYear,
            @RequestParam(required = false) Boolean pending) {

        if (academicYear != null && pending != null && pending) {
            List<FeeJournalDTO> journals = feeJournalService.getPendingFeesByAcademicYear(academicYear);
            return ResponseEntity.ok(journals);
        }

        List<FeeJournalDTO> journals = feeJournalService.getAllJournals();
        return ResponseEntity.ok(journals);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<FeeJournalDTO>> getJournalsByStudentId(
            @PathVariable Long studentId,
            @RequestParam(required = false) Boolean pending) {

        if (pending != null && pending) {
            List<FeeJournalDTO> journals = feeJournalService.getPendingFeesByStudentId(studentId);
            return ResponseEntity.ok(journals);
        }

        List<FeeJournalDTO> journals = feeJournalService.getJournalsByStudentId(studentId);
        return ResponseEntity.ok(journals);
    }

    @GetMapping("/student/{studentId}/pending-count")
    public ResponseEntity<Long> getPendingFeesCount(@PathVariable Long studentId) {
        Long count = feeJournalService.getPendingFeesCount(studentId);
        return ResponseEntity.ok(count);
    }
}
