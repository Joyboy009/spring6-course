package com.lee.walletwise.service;

import com.lee.walletwise.dto.AddMoneyRequest;
import com.lee.walletwise.dto.TransferRequest;
import com.lee.walletwise.dto.WalletBalanceResponse;
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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {
    
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final RabbitProducerService rabbitProducerService;
    
    public WalletBalanceResponse getBalance() {
        User currentUser = getCurrentUser();
        Wallet wallet = walletRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        
        return new WalletBalanceResponse(
                wallet.getId(),
                wallet.getWalletNumber(),
                wallet.getBalance(),
                currentUser.getUsername(),
                currentUser.getFullName()
        );
    }
    
    @Transactional
    public WalletBalanceResponse addMoney(AddMoneyRequest request) {
        User currentUser = getCurrentUser();
        Wallet wallet = walletRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        
        // Update wallet balance
        wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        walletRepository.save(wallet);
        
        // Create transaction record
        Transaction transaction = new Transaction();
        transaction.setReceiverWallet(wallet);
        transaction.setAmount(request.getAmount());
        transaction.setTransactionType(Transaction.TransactionType.CREDIT);
        transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
        transaction.setDescription(request.getDescription() != null ? request.getDescription() : "Money added to wallet");
        
        transactionRepository.save(transaction);
        
        // Send message to RabbitMQ
        rabbitProducerService.sendTransactionMessage(
                "Transaction successful for user " + currentUser.getId() + " - Added " + request.getAmount()
        );
        
        log.info("Money added successfully to wallet: {} - Amount: {}", wallet.getWalletNumber(), request.getAmount());
        
        return new WalletBalanceResponse(
                wallet.getId(),
                wallet.getWalletNumber(),
                wallet.getBalance(),
                currentUser.getUsername(),
                currentUser.getFullName()
        );
    }
    
    @Transactional
    public WalletBalanceResponse transferMoney(TransferRequest request) {
        User currentUser = getCurrentUser();
        Wallet senderWallet = walletRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Sender wallet not found"));
        
        // Find recipient wallet by username or wallet number
        Wallet receiverWallet = walletRepository.findByUsername(request.getRecipientIdentifier())
                .orElse(walletRepository.findByWalletNumber(request.getRecipientIdentifier())
                        .orElseThrow(() -> new RuntimeException("Recipient not found")));
        
        // Check if sender has sufficient balance
        if (senderWallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        
        // Check if sender is not transferring to themselves
        if (senderWallet.getId().equals(receiverWallet.getId())) {
            throw new RuntimeException("Cannot transfer money to yourself");
        }
        
        // Update balances
        senderWallet.setBalance(senderWallet.getBalance().subtract(request.getAmount()));
        receiverWallet.setBalance(receiverWallet.getBalance().add(request.getAmount()));
        
        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);
        
        // Create transaction record
        Transaction transaction = new Transaction();
        transaction.setSenderWallet(senderWallet);
        transaction.setReceiverWallet(receiverWallet);
        transaction.setAmount(request.getAmount());
        transaction.setTransactionType(Transaction.TransactionType.TRANSFER);
        transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
        transaction.setDescription(request.getDescription() != null ? request.getDescription() : "Money transfer");
        
        transactionRepository.save(transaction);
        
        // Send message to RabbitMQ
        rabbitProducerService.sendTransactionMessage(
                "Transaction successful for user " + currentUser.getId() + " - Transferred " + request.getAmount() + 
                " to " + receiverWallet.getUser().getUsername()
        );
        
        log.info("Money transferred successfully from {} to {} - Amount: {}", 
                senderWallet.getWalletNumber(), receiverWallet.getWalletNumber(), request.getAmount());
        
        return new WalletBalanceResponse(
                senderWallet.getId(),
                senderWallet.getWalletNumber(),
                senderWallet.getBalance(),
                currentUser.getUsername(),
                currentUser.getFullName()
        );
    }
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }
}