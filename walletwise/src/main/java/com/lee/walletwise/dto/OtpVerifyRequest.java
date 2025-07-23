package com.lee.walletwise.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record OtpVerifyRequest(
        @Email @NotBlank String email,
        @NotBlank String otp
) {
}