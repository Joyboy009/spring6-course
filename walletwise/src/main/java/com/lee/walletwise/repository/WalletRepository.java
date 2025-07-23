package com.lee.walletwise.repository;

import com.lee.walletwise.entity.User;
import com.lee.walletwise.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUser(User user);
}