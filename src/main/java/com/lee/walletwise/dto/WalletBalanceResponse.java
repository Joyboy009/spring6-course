package com.lee.walletwise.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletBalanceResponse {
    
    private Long walletId;
    private String walletNumber;
    private BigDecimal balance;
    private String username;
    private String fullName;
}