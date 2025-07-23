package com.lee.walletwise.service;

import com.lee.walletwise.dto.AddMoneyRequest;
import com.lee.walletwise.dto.TransferRequest;
import com.lee.walletwise.entity.*;
import com.lee.walletwise.repository.TransactionRepository;
import com.lee.walletwise.repository.UserRepository;
import com.lee.walletwise.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final RabbitProducerService rabbitProducerService;

    public BigDecimal getBalance(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Wallet not found"));
        return wallet.getBalance();
    }

    @Transactional
    public void addMoney(String email, AddMoneyRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Wallet not found"));

        wallet.setBalance(wallet.getBalance().add(request.amount()));
        walletRepository.save(wallet);

        Transaction tx = Transaction.builder()
                .wallet(wallet)
                .amount(request.amount())
                .type(TransactionType.CREDIT)
                .description("Add money")
                .timestamp(LocalDateTime.now())
                .build();
        transactionRepository.save(tx);

        rabbitProducerService.publishTransactionSuccess(user.getId());
    }

    @Transactional
    public void transferMoney(String senderEmail, TransferRequest request) {
        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        User receiver = userRepository.findByEmail(request.receiverEmail())
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        Wallet senderWallet = walletRepository.findByUser(sender)
                .orElseThrow(() -> new IllegalStateException("Sender wallet not found"));
        Wallet receiverWallet = walletRepository.findByUser(receiver)
                .orElseThrow(() -> new IllegalStateException("Receiver wallet not found"));

        BigDecimal amount = request.amount();
        if (senderWallet.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient balance");
        }

        senderWallet.setBalance(senderWallet.getBalance().subtract(amount));
        receiverWallet.setBalance(receiverWallet.getBalance().add(amount));

        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);

        LocalDateTime now = LocalDateTime.now();
        Transaction debitTx = Transaction.builder()
                .wallet(senderWallet)
                .amount(amount)
                .type(TransactionType.DEBIT)
                .description("Transfer to " + receiver.getEmail())
                .timestamp(now)
                .build();

        Transaction creditTx = Transaction.builder()
                .wallet(receiverWallet)
                .amount(amount)
                .type(TransactionType.CREDIT)
                .description("Transfer from " + sender.getEmail())
                .timestamp(now)
                .build();

        transactionRepository.save(debitTx);
        transactionRepository.save(creditTx);

        rabbitProducerService.publishTransactionSuccess(sender.getId());
    }

    public List<Transaction> history(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Wallet not found"));
        return transactionRepository.findByWalletOrderByTimestampDesc(wallet);
    }
}