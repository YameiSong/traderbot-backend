package com.cryptoai.traderbot.service;

import com.cryptoai.traderbot.domain.OrderType;
import com.cryptoai.traderbot.domain.WalletTransactionType;
import com.cryptoai.traderbot.model.Order;
import com.cryptoai.traderbot.model.User;
import com.cryptoai.traderbot.model.Wallet;
import com.cryptoai.traderbot.model.WalletTransaction;
import com.cryptoai.traderbot.repository.WalletRepository;
import com.cryptoai.traderbot.repository.WalletTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
public class WalletServiceImpl implements WalletService{

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletTransactionRepository walletTransactionRepository;

    @Override
    public Wallet getUserWallet(User user) {
        Wallet wallet = walletRepository.findByUserId(user.getId());
        if (wallet == null) {
            wallet = new Wallet();
            wallet.setUser(user);
            walletRepository.save(wallet);
        }
        return wallet;
    }

    @Override
    public Wallet addBalance(Wallet wallet, Long amount) {
        BigDecimal balance = wallet.getBalance();
        BigDecimal newBalance = balance.add(BigDecimal.valueOf(amount));
        wallet.setBalance(newBalance);
        return walletRepository.save(wallet);
    }

    @Override
    public Wallet findWalletById(Long id) throws Exception {
        return walletRepository.findById(id).orElseThrow(() -> new Exception("Wallet not found"));
    }

    @Override
    public Wallet walletToWalletTransfer(User sender, Wallet receiverWallet, Long amount, String purpose) throws Exception {
        Wallet senderWallet = getUserWallet(sender);
        if (senderWallet.getBalance().compareTo(BigDecimal.valueOf(amount)) < 0) {
            throw new Exception("Insufficient balance");
        }
        BigDecimal senderBalance = senderWallet.getBalance().subtract(BigDecimal.valueOf(amount));
        senderWallet.setBalance(senderBalance);
        walletRepository.save(senderWallet);

        BigDecimal receiverBalance = receiverWallet.getBalance().add(BigDecimal.valueOf(amount));
        receiverWallet.setBalance(receiverBalance);
        walletRepository.save(receiverWallet);

        WalletTransaction senderTransaction = new WalletTransaction();
        senderTransaction.setWallet(senderWallet);
        senderTransaction.setType(WalletTransactionType.TRANSFER);
        senderTransaction.setDate(LocalDate.now());
        senderTransaction.setPurpose(purpose);
        senderTransaction.setAmount(-amount);
        senderTransaction.setTransferId(UUID.randomUUID().toString()); // Generate a random transfer ID
        walletTransactionRepository.save(senderTransaction);

        WalletTransaction receiverTransaction = new WalletTransaction();
        receiverTransaction.setWallet(receiverWallet);
        receiverTransaction.setType(WalletTransactionType.TRANSFER);
        receiverTransaction.setDate(LocalDate.now());
        receiverTransaction.setPurpose(purpose);
        receiverTransaction.setAmount(amount);
        receiverTransaction.setTransferId(senderTransaction.getTransferId()); // Use the same transfer ID
        walletTransactionRepository.save(receiverTransaction);

        return senderWallet;
    }

    @Override
    public Wallet makeOrderPayment(Order order, User user) {
        Wallet wallet = getUserWallet(user);
        BigDecimal newBalance;
        if (order.getOrderType().equals(OrderType.BUY)) {
            newBalance = wallet.getBalance().subtract(order.getPrice());
            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("Insufficient balance for this order");
            }
        } else {
            newBalance = wallet.getBalance().add(order.getPrice());
        }
        wallet.setBalance(newBalance);
        return walletRepository.save(wallet);
    }
}
