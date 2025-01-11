package com.cryptoai.traderbot.service;

import com.cryptoai.traderbot.domain.WalletTransactionType;
import com.cryptoai.traderbot.model.Wallet;
import com.cryptoai.traderbot.model.WalletTransaction;

import java.util.List;

public interface TransactionService {
    List<WalletTransaction> getTransactionsByWallet(Wallet wallet) throws Exception;
    WalletTransaction createTransaction(Wallet wallet, WalletTransactionType type, Long transferId, String purpose, Long amount) throws Exception;
}
