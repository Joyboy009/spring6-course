package com.lee.walletwise.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record OtpSendRequest(@Email @NotBlank String email) {
}