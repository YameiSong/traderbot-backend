package com.cryptoai.traderbot.service;

import com.cryptoai.traderbot.domain.WalletTransactionType;
import com.cryptoai.traderbot.model.Wallet;
import com.cryptoai.traderbot.model.WalletTransaction;
import com.cryptoai.traderbot.repository.WalletTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService{

    @Autowired
    private WalletTransactionRepository walletTransactionRepository;

    @Override
    public List<WalletTransaction> getTransactionsByWallet(Wallet wallet) throws Exception {
        Long walletId = wallet.getId();
        return walletTransactionRepository.findByWalletId(walletId);
    }

    @Override
    public WalletTransaction createTransaction(Wallet wallet, WalletTransactionType type, Long transferId, String purpose, Long amount) throws Exception {
        if (wallet == null) {
            throw new Exception("Wallet cannot be null.");
        }

        if (amount == null || amount <= 0) {
            throw new Exception("Transaction amount must be greater than zero.");
        }

        WalletTransaction transaction = new WalletTransaction();
        transaction.setWallet(wallet);
        transaction.setType(type);
        transaction.setDate(LocalDate.now());
        transaction.setPurpose(purpose);
        transaction.setAmount(amount);

        if (transferId != null) {
            transaction.setTransferId(String.valueOf(transferId));
        } else {
            transaction.setTransferId(UUID.randomUUID().toString());
        }

        return walletTransactionRepository.save(transaction);
    }
}
