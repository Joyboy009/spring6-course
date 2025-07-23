package com.lee.walletwise.controller;

import com.lee.walletwise.dto.AddMoneyRequest;
import com.lee.walletwise.dto.ApiResponse;
import com.lee.walletwise.dto.TransferRequest;
import com.lee.walletwise.dto.WalletBalanceResponse;
import com.lee.walletwise.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class WalletController {
    
    private final WalletService walletService;
    
    @GetMapping("/balance")
    public ResponseEntity<ApiResponse<WalletBalanceResponse>> getBalance() {
        try {
            WalletBalanceResponse response = walletService.getBalance();
            return ResponseEntity.ok(ApiResponse.success("Balance retrieved successfully", response));
        } catch (Exception e) {
            log.error("Failed to get balance: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<WalletBalanceResponse>> addMoney(@Valid @RequestBody AddMoneyRequest request) {
        try {
            WalletBalanceResponse response = walletService.addMoney(request);
            return ResponseEntity.ok(ApiResponse.success("Money added successfully", response));
        } catch (Exception e) {
            log.error("Failed to add money: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<WalletBalanceResponse>> transferMoney(@Valid @RequestBody TransferRequest request) {
        try {
            WalletBalanceResponse response = walletService.transferMoney(request);
            return ResponseEntity.ok(ApiResponse.success("Money transferred successfully", response));
        } catch (Exception e) {
            log.error("Failed to transfer money: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}