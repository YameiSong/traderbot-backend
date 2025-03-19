package com.cryptoai.traderbot.controller;

import com.cryptoai.traderbot.domain.PaymentMethod;
import com.cryptoai.traderbot.model.PaymentOrder;
import com.cryptoai.traderbot.model.User;
import com.cryptoai.traderbot.response.PaymentResponse;
import com.cryptoai.traderbot.service.PaymentService;
import com.cryptoai.traderbot.service.UserService;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class PaymentController {

    @Autowired
    private UserService userService;

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/api/payment/{paymentMethod}/amount/{amount}")
    public ResponseEntity<PaymentResponse> paymentHandler(
            @PathVariable PaymentMethod paymentMethod,
            @PathVariable Long amount,
            @RequestHeader("Authorization") String jwt
            ) throws Exception, StripeException {
        User user = userService.findUserByJwt(jwt);
        PaymentResponse paymentResponse;
        PaymentOrder order = paymentService.createOrder(user, amount, paymentMethod);

        if (paymentMethod.equals(PaymentMethod.Stripe)) {
            paymentResponse = paymentService.createStripePaymentLink(user, amount, order.getId());
        } else {
            throw new Exception("Unknown payment method");
        }

        return new ResponseEntity<>(paymentResponse, HttpStatus.CREATED);
    }

    @GetMapping("/api/retrieve-payment-intent/{sessionId}")
    public ResponseEntity<Map<String, String>> getPaymentIntent(@PathVariable String sessionId) throws StripeException {
        String paymentIntentId = paymentService.getPaymentIntent(sessionId);

        Map<String, String> response = new HashMap<>();
        response.put("payment_id", paymentIntentId);

        return ResponseEntity.ok(response);
    }
}
