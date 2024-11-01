package com.cryptoai.traderbot.service;

import com.cryptoai.traderbot.model.Order;
import com.cryptoai.traderbot.model.User;
import com.cryptoai.traderbot.model.Wallet;

public interface WalletService {
    Wallet getUserWallet(User user);
    Wallet addBalance(Wallet wallet, Long amount);
    Wallet findWalletById(Long id) throws Exception;
    Wallet walletToWalletTransfer(User sender, Wallet receiverWallet, Long amount) throws Exception;
    Wallet makeOrderPayment(Order order, User user);
}
