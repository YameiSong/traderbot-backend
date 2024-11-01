package com.cryptoai.traderbot.controller;

import com.cryptoai.traderbot.domain.OrderType;
import com.cryptoai.traderbot.model.Coin;
import com.cryptoai.traderbot.model.Order;
import com.cryptoai.traderbot.model.User;
import com.cryptoai.traderbot.request.CreateOrderRequest;
import com.cryptoai.traderbot.service.CoinService;
import com.cryptoai.traderbot.service.OrderService;
import com.cryptoai.traderbot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private CoinService coinService;

    @PostMapping("/pay")
    public ResponseEntity<Order> makeOrderPayment(@RequestHeader("Authorization") String jwt, @RequestBody CreateOrderRequest req) throws Exception {
        User user = userService.findUserByJwt(jwt);
        Coin coin = coinService.findById(req.getCoinId());
        Order order = orderService.processOrder(coin, req.getQuantity(), req.getOrderType(), user);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@RequestHeader("Authorization") String jwt, @PathVariable Long orderId) throws Exception {
        User user = userService.findUserByJwt(jwt);
        Order order = orderService.getOrderById(orderId);

        if (order.getUser().getId().equals(user.getId())) {
            return new ResponseEntity<>(order, HttpStatus.OK);
        }

        throw new Exception("You don't have access to this order");
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrdersOfUser(
            @RequestHeader("Authorization") String jwt,
            @RequestParam(required = false) OrderType order_type,
            @RequestParam(required = false) String asset_symbol) throws Exception {
        Long userId = userService.findUserByJwt(jwt).getId();
        List<Order> orders = orderService.getAllOrdersOfUser(userId, order_type, asset_symbol);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }
}
