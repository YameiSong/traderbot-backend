package com.cryptoai.traderbot.response;

import lombok.Data;

@Data
public class PaymentResponse {
    private String paymentURL;
    private String paymentId;
}
