package com.cryptoai.traderbot.request;

import com.cryptoai.traderbot.domain.OrderType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CreateOrderRequest {
    @JsonProperty("coinId")
    private String coinId;

    private Double quantity;

    @JsonProperty("orderType")
    private OrderType orderType;
}
