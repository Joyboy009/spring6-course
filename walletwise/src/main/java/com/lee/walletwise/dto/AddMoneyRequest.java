package com.lee.walletwise.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record AddMoneyRequest(
        @NotNull @DecimalMin("0.01") BigDecimal amount
) {
}