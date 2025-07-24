package com.lee.walletwise.repository;

import com.lee.walletwise.entity.Transaction;
import com.lee.walletwise.entity.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    Optional<Transaction> findByTransactionReference(String transactionReference);
    
    @Query("SELECT t FROM Transaction t WHERE t.senderWallet = :wallet OR t.receiverWallet = :wallet ORDER BY t.createdAt DESC")
    List<Transaction> findByWallet(@Param("wallet") Wallet wallet);
    
    @Query("SELECT t FROM Transaction t WHERE t.senderWallet = :wallet OR t.receiverWallet = :wallet ORDER BY t.createdAt DESC")
    Page<Transaction> findByWallet(@Param("wallet") Wallet wallet, Pageable pageable);
    
    @Query("SELECT t FROM Transaction t WHERE t.senderWallet.id = :walletId OR t.receiverWallet.id = :walletId ORDER BY t.createdAt DESC")
    List<Transaction> findByWalletId(@Param("walletId") Long walletId);
    
    @Query("SELECT t FROM Transaction t WHERE t.senderWallet.user.id = :userId OR t.receiverWallet.user.id = :userId ORDER BY t.createdAt DESC")
    List<Transaction> findByUserId(@Param("userId") Long userId);
    
    @Query("SELECT t FROM Transaction t WHERE t.status = :status")
    List<Transaction> findByStatus(@Param("status") Transaction.TransactionStatus status);
    
    @Query("SELECT t FROM Transaction t WHERE t.transactionType = :type")
    List<Transaction> findByTransactionType(@Param("type") Transaction.TransactionType type);
    
    @Query("SELECT t FROM Transaction t WHERE t.createdAt BETWEEN :startDate AND :endDate ORDER BY t.createdAt DESC")
    List<Transaction> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT t FROM Transaction t WHERE (t.senderWallet = :wallet OR t.receiverWallet = :wallet) AND t.createdAt BETWEEN :startDate AND :endDate ORDER BY t.createdAt DESC")
    List<Transaction> findByWalletAndDateRange(@Param("wallet") Wallet wallet, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}