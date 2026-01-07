package com.school.management.controller;

import com.school.management.dto.FeeReceiptDTO;
import com.school.management.service.FeeReceiptService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fee-receipts")
@RequiredArgsConstructor
public class FeeReceiptController {

    private final FeeReceiptService feeReceiptService;

    @PostMapping
    public ResponseEntity<FeeReceiptDTO> generateReceipt(@Valid @RequestBody FeeReceiptDTO receiptDTO) {
        FeeReceiptDTO created = feeReceiptService.generateReceipt(receiptDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeeReceiptDTO> getReceiptById(@PathVariable Long id) {
        FeeReceiptDTO receipt = feeReceiptService.getReceiptById(id);
        return ResponseEntity.ok(receipt);
    }

    @GetMapping("/receipt-number/{receiptNumber}")
    public ResponseEntity<FeeReceiptDTO> getReceiptByReceiptNumber(@PathVariable String receiptNumber) {
        FeeReceiptDTO receipt = feeReceiptService.getReceiptByReceiptNumber(receiptNumber);
        return ResponseEntity.ok(receipt);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<FeeReceiptDTO>> getReceiptsByStudentId(@PathVariable Long studentId) {
        List<FeeReceiptDTO> receipts = feeReceiptService.getReceiptsByStudentId(studentId);
        return ResponseEntity.ok(receipts);
    }

    @GetMapping
    public ResponseEntity<List<FeeReceiptDTO>> getAllReceipts() {
        List<FeeReceiptDTO> receipts = feeReceiptService.getAllReceipts();
        return ResponseEntity.ok(receipts);
    }
}
