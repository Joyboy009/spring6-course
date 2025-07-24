package com.lee.walletwise.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpRequest {
    
    @NotBlank(message = "Phone number or email is required")
    private String identifier; // Can be phone number or email
    
    private String otp; // For verification requests
}