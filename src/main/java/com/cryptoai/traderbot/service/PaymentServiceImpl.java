package com.cryptoai.traderbot.service;

import com.cryptoai.traderbot.domain.PaymentMethod;
import com.cryptoai.traderbot.domain.PaymentOrderStatus;
import com.cryptoai.traderbot.model.PaymentOrder;
import com.cryptoai.traderbot.model.User;
import com.cryptoai.traderbot.repository.PaymentOrderRepository;
import com.cryptoai.traderbot.response.PaymentResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private PaymentOrderRepository paymentOrderRepository;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Override
    public PaymentOrder createOrder(User user, Long amount, PaymentMethod paymentMethod) {
        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setUser(user);
        paymentOrder.setAmount(amount);
        paymentOrder.setPaymentMethod(paymentMethod);
        paymentOrder.setStatus(PaymentOrderStatus.PENDING);
        return paymentOrderRepository.save(paymentOrder);
    }

    @Override
    public PaymentOrder getPaymentOrderById(Long id) throws Exception {
        return paymentOrderRepository.findById(id).orElseThrow(() -> new Exception("Payment order not found"));
    }

    @Override
    public Boolean ProceedPaymentOrder(PaymentOrder paymentOrder, String paymentId) {
        if (paymentOrder.getStatus() == null) {
            paymentOrder.setStatus(PaymentOrderStatus.PENDING);
        }
        if (paymentOrder.getStatus().equals(PaymentOrderStatus.PENDING)) {
            if (paymentOrder.getPaymentMethod().equals(PaymentMethod.STRIPE)) {
                try {
                    Stripe.apiKey = stripeApiKey;

                    PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentId);

                    if ("succeeded".equals(paymentIntent.getStatus())) {
                        paymentOrder.setStatus(PaymentOrderStatus.SUCCESS);
                        paymentOrderRepository.save(paymentOrder);
                        return true;
                    }

                    paymentOrder.setStatus(PaymentOrderStatus.FAILED);
                    paymentOrderRepository.save(paymentOrder);
                } catch (StripeException e) {
                    System.err.println("Stripe Exception: " + e.getMessage());
                }
            }
        }
        return false;
    }

    @Override
    public PaymentResponse createStripePaymentLink(User user, Long amount, Long orderId) throws StripeException {
        Stripe.apiKey = stripeApiKey;

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:5454/wallet?order_id="+orderId)
                .setCancelUrl("http://localhost:5454/payment/cancel")
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(
                                SessionCreateParams.LineItem.PriceData.builder()
                                        .setCurrency("usd")
                                        .setUnitAmount(amount*100) // unit: cent
                                        .setProductData(SessionCreateParams
                                                .LineItem
                                                .PriceData
                                                .ProductData
                                                .builder()
                                                .setName("Top up wallet")
                                                .build()
                                        ).build()
                        ).build()
                ).build();

        Session session = Session.create(params);

        System.out.println("session ___ " + session);

        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setPaymentURL(session.getUrl());
        return paymentResponse;
    }
}
