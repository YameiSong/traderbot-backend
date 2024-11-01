package com.cryptoai.traderbot.service;

import com.cryptoai.traderbot.domain.OrderType;
import com.cryptoai.traderbot.model.Coin;
import com.cryptoai.traderbot.model.Order;
import com.cryptoai.traderbot.model.OrderItem;
import com.cryptoai.traderbot.model.User;

import java.util.List;

public interface OrderService {

    Order createOrder(User user, OrderItem orderItem, OrderType orderType);

    Order getOrderById(Long id) throws Exception;

    List<Order> getAllOrdersOfUser(Long userId, OrderType orderType, String assetSymbol);

    Order processOrder(Coin coin, Double quantity, OrderType orderType, User user) throws Exception;
}
