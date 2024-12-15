package com.cryptoai.traderbot.service;

import com.cryptoai.traderbot.model.PaymentDetails;
import com.cryptoai.traderbot.model.User;

public interface PaymentDetailService {
    public PaymentDetails addPaymentDetails(String accountNumber, String accountHolder, String ifsc, String bankName, User user);

    public PaymentDetails getUsersPaymentDetails(User user);
}
