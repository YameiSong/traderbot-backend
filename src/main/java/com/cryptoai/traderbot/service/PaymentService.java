package com.cryptoai.traderbot.service;

import com.cryptoai.traderbot.domain.PaymentMethod;
import com.cryptoai.traderbot.model.PaymentOrder;
import com.cryptoai.traderbot.model.User;
import com.cryptoai.traderbot.response.PaymentResponse;
import com.stripe.exception.StripeException;

public interface PaymentService {
    PaymentOrder createOrder(User user, Long amount, PaymentMethod paymentMethod);
    PaymentOrder getPaymentOrderById(Long id) throws Exception;
    Boolean ProceedPaymentOrder(PaymentOrder paymentOrder, String paymentId);
    PaymentResponse createStripePaymentLink(User user, Long amount, Long orderId) throws StripeException;
    String getPaymentIntent(String sessionId) throws StripeException;
}
