package com.lee.walletwise.repository;

import com.lee.walletwise.entity.Transaction;
import com.lee.walletwise.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByWalletOrderByTimestampDesc(Wallet wallet);
}