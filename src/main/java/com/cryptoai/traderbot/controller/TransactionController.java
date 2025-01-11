package com.cryptoai.traderbot.controller;

import com.cryptoai.traderbot.model.User;
import com.cryptoai.traderbot.model.Wallet;
import com.cryptoai.traderbot.model.WalletTransaction;
import com.cryptoai.traderbot.service.TransactionService;
import com.cryptoai.traderbot.service.UserService;
import com.cryptoai.traderbot.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TransactionController {

    @Autowired
    private WalletService walletService;

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    @GetMapping("api/transactions")
    public ResponseEntity<List<WalletTransaction>> getUserWallet(
            @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwt(jwt);
        Wallet wallet = walletService.getUserWallet(user);
        List<WalletTransaction> transactions = transactionService.getTransactionsByWallet(wallet);

        return ResponseEntity.ok(transactions);
    }
}
