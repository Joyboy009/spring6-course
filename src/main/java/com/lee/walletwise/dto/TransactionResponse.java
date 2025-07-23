package com.lee.walletwise.dto;

import com.lee.walletwise.entity.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    
    private Long id;
    private String transactionReference;
    private BigDecimal amount;
    private Transaction.TransactionType transactionType;
    private Transaction.TransactionStatus status;
    private String description;
    private String senderWalletNumber;
    private String senderUsername;
    private String receiverWalletNumber;
    private String receiverUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}