package com.lee.walletwise.repository;

import com.lee.walletwise.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    
    Optional<Wallet> findByUserId(Long userId);
    
    Optional<Wallet> findByWalletNumber(String walletNumber);
    
    @Query("SELECT w FROM Wallet w WHERE w.user.username = :username")
    Optional<Wallet> findByUsername(@Param("username") String username);
    
    @Query("SELECT w FROM Wallet w WHERE w.user.email = :email")
    Optional<Wallet> findByUserEmail(@Param("email") String email);
    
    boolean existsByWalletNumber(String walletNumber);
    
    @Query("SELECT w FROM Wallet w WHERE w.isActive = true")
    java.util.List<Wallet> findAllActiveWallets();
}