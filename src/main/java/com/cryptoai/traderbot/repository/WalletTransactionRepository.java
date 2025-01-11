package com.cryptoai.traderbot.repository;

import com.cryptoai.traderbot.model.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {
    List<WalletTransaction> findByWalletId(Long walletId);
}
