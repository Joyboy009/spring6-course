package com.lee.walletwise.controller;

import com.lee.walletwise.dto.OtpSendRequest;
import com.lee.walletwise.dto.OtpVerifyRequest;
import com.lee.walletwise.service.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/otp")
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;

    @PostMapping("/send")
    public ResponseEntity<String> send(@Valid @RequestBody OtpSendRequest request) {
        String otp = otpService.generateAndStoreOtp(request.email());
        // For demo purposes return OTP in response (do not do this in production)
        return ResponseEntity.ok("OTP sent: " + otp);
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verify(@Valid @RequestBody OtpVerifyRequest request) {
        boolean valid = otpService.verifyOtp(request.email(), request.otp());
        if (valid) {
            return ResponseEntity.ok("OTP verified successfully");
        }
        return ResponseEntity.badRequest().body("Invalid or expired OTP");
    }
}