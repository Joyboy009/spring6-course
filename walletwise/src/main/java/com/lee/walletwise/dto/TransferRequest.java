package com.lee.walletwise.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record TransferRequest(
        @Email @NotBlank String receiverEmail,
        @NotNull @DecimalMin("0.01") BigDecimal amount
) {
}