package com.lee.walletwise.service;

import com.lee.walletwise.dto.TransactionResponse;
import com.lee.walletwise.entity.Transaction;
import com.lee.walletwise.entity.User;
import com.lee.walletwise.entity.Wallet;
import com.lee.walletwise.repository.TransactionRepository;
import com.lee.walletwise.repository.UserRepository;
import com.lee.walletwise.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    
    public List<TransactionResponse> getTransactionHistory() {
        User currentUser = getCurrentUser();
        Wallet wallet = walletRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        
        List<Transaction> transactions = transactionRepository.findByWallet(wallet);
        
        return transactions.stream()
                .map(this::convertToTransactionResponse)
                .collect(Collectors.toList());
    }
    
    public TransactionResponse getTransactionByReference(String transactionReference) {
        Transaction transaction = transactionRepository.findByTransactionReference(transactionReference)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        // Verify that the current user is involved in this transaction
        User currentUser = getCurrentUser();
        Wallet currentUserWallet = walletRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        
        boolean isInvolved = (transaction.getSenderWallet() != null && 
                             transaction.getSenderWallet().getId().equals(currentUserWallet.getId())) ||
                            (transaction.getReceiverWallet() != null && 
                             transaction.getReceiverWallet().getId().equals(currentUserWallet.getId()));
        
        if (!isInvolved) {
            throw new RuntimeException("Transaction not found");
        }
        
        return convertToTransactionResponse(transaction);
    }
    
    private TransactionResponse convertToTransactionResponse(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setTransactionReference(transaction.getTransactionReference());
        response.setAmount(transaction.getAmount());
        response.setTransactionType(transaction.getTransactionType());
        response.setStatus(transaction.getStatus());
        response.setDescription(transaction.getDescription());
        response.setCreatedAt(transaction.getCreatedAt());
        response.setUpdatedAt(transaction.getUpdatedAt());
        
        if (transaction.getSenderWallet() != null) {
            response.setSenderWalletNumber(transaction.getSenderWallet().getWalletNumber());
            response.setSenderUsername(transaction.getSenderWallet().getUser().getUsername());
        }
        
        if (transaction.getReceiverWallet() != null) {
            response.setReceiverWalletNumber(transaction.getReceiverWallet().getWalletNumber());
            response.setReceiverUsername(transaction.getReceiverWallet().getUser().getUsername());
        }
        
        return response;
    }
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }
}