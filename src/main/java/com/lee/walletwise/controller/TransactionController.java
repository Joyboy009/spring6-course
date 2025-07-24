package com.lee.walletwise.controller;

import com.lee.walletwise.dto.ApiResponse;
import com.lee.walletwise.dto.TransactionResponse;
import com.lee.walletwise.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class TransactionController {
    
    private final TransactionService transactionService;
    
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactionHistory() {
        try {
            List<TransactionResponse> transactions = transactionService.getTransactionHistory();
            return ResponseEntity.ok(ApiResponse.success("Transaction history retrieved successfully", transactions));
        } catch (Exception e) {
            log.error("Failed to get transaction history: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/transaction/{transactionReference}")
    public ResponseEntity<ApiResponse<TransactionResponse>> getTransactionByReference(
            @PathVariable String transactionReference) {
        try {
            TransactionResponse transaction = transactionService.getTransactionByReference(transactionReference);
            return ResponseEntity.ok(ApiResponse.success("Transaction retrieved successfully", transaction));
        } catch (Exception e) {
            log.error("Failed to get transaction: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}