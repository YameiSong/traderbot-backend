package com.cryptoai.traderbot.request;

import com.cryptoai.traderbot.domain.OrderType;
import lombok.Data;

@Data
public class CreateOrderRequest {
    private String coinId;
    private Double quantity;
    private OrderType orderType;
}
