package com.lee.walletwise.controller;

import com.lee.walletwise.dto.ApiResponse;
import com.lee.walletwise.dto.OtpRequest;
import com.lee.walletwise.service.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/otp")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class OtpController {
    
    private final OtpService otpService;
    
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<Map<String, Object>>> sendOtp(@Valid @RequestBody OtpRequest request) {
        try {
            String otp = otpService.generateAndStoreOtp(request.getIdentifier());
            
            // In a real application, you would send the OTP via SMS or email
            // For demo purposes, we'll return it in the response
            Map<String, Object> response = new HashMap<>();
            response.put("message", "OTP sent successfully");
            response.put("otp", otp); // Remove this in production
            response.put("expiresIn", "5 minutes");
            
            log.info("OTP sent to: {}", request.getIdentifier());
            
            return ResponseEntity.ok(ApiResponse.success("OTP sent successfully", response));
        } catch (Exception e) {
            log.error("Failed to send OTP: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verifyOtp(@Valid @RequestBody OtpRequest request) {
        try {
            if (request.getOtp() == null || request.getOtp().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("OTP is required"));
            }
            
            boolean isValid = otpService.verifyOtp(request.getIdentifier(), request.getOtp());
            
            Map<String, Object> response = new HashMap<>();
            response.put("valid", isValid);
            response.put("message", isValid ? "OTP verified successfully" : "Invalid or expired OTP");
            
            if (isValid) {
                log.info("OTP verified successfully for: {}", request.getIdentifier());
                return ResponseEntity.ok(ApiResponse.success("OTP verified successfully", response));
            } else {
                log.warn("OTP verification failed for: {}", request.getIdentifier());
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Invalid or expired OTP"));
            }
        } catch (Exception e) {
            log.error("Failed to verify OTP: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}