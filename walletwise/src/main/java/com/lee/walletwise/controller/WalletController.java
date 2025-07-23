package com.lee.walletwise.controller;

import com.lee.walletwise.dto.AddMoneyRequest;
import com.lee.walletwise.dto.TransferRequest;
import com.lee.walletwise.entity.Transaction;
import com.lee.walletwise.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/balance")
    public ResponseEntity<BigDecimal> balance(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(walletService.getBalance(user.getUsername()));
    }

    @PostMapping("/add")
    public ResponseEntity<Void> add(@AuthenticationPrincipal UserDetails user,
                                    @Valid @RequestBody AddMoneyRequest request) {
        walletService.addMoney(user.getUsername(), request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/transfer")
    public ResponseEntity<Void> transfer(@AuthenticationPrincipal UserDetails user,
                                         @Valid @RequestBody TransferRequest request) {
        walletService.transferMoney(user.getUsername(), request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/history")
    public ResponseEntity<List<Transaction>> history(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(walletService.history(user.getUsername()));
    }
}