package com.cryptoai.traderbot.service;

import com.cryptoai.traderbot.domain.OrderStatus;
import com.cryptoai.traderbot.domain.OrderType;
import com.cryptoai.traderbot.model.*;
import com.cryptoai.traderbot.repository.OrderItemRepository;
import com.cryptoai.traderbot.repository.OrderRepository;
import com.cryptoai.traderbot.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private WalletService walletService;

    @Autowired
    private AssetService assetService;

    @Override
    public Order createOrder(User user, OrderItem orderItem, OrderType orderType) {
        double value = orderItem.getCoin().getCurrentPrice() * orderItem.getQuantity();

        Order order = new Order();
        order.setUser(user);
        order.setOrderItem(orderItem);
        order.setOrderType(orderType);
        order.setPrice(BigDecimal.valueOf(value));
        order.setTimestamp(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.PENDING);

        return orderRepository.save(order);
    }

    @Override
    public Order getOrderById(Long id) throws Exception {
        return orderRepository.findById(id).orElseThrow(() -> new Exception("Order not found"));
    }

    @Override
    public List<Order> getAllOrdersOfUser(Long userId, OrderType orderType, String assetSymbol) {
        // TODO: OrderType orderType, String assetSymbol are not used
        return orderRepository.findByUserId(userId);
    }

    private OrderItem createOrderItem(Coin coin, Double quantity, Double buyPrice, Double sellPrice) {
        OrderItem orderItem = new OrderItem();
        orderItem.setCoin(coin);
        orderItem.setQuantity(quantity);
        orderItem.setBuyPrice(buyPrice);
        orderItem.setSellPrice(sellPrice);
        return orderItemRepository.save(orderItem);
    }

    @Transactional
    public Order buyAsset(Coin coin, Double quantity, User user) throws Exception {
        if (quantity <= 0) {
            throw new Exception("Quantity should be > 0");
        }

        double buyPrice = coin.getCurrentPrice();

        OrderItem orderItem = createOrderItem(coin, quantity, buyPrice, 0.0);

        Order order = createOrder(user, orderItem, OrderType.BUY);
        orderItem.setOrder(order);

        walletService.makeOrderPayment(order, user);

        order.setOrderStatus(OrderStatus.SUCCESS);
        order.setOrderType(OrderType.BUY);
        Order savedOrder = orderRepository.save(order);

        Asset oldAsset = assetService.findAssetByUserIdAndCoinId(user.getId(), coin.getId());

        if (oldAsset == null) {
            assetService.createAsset(user, coin, quantity);
        }
        else {
            assetService.updateAsset(oldAsset.getId(), oldAsset.getQuantity() + quantity);
        }

        return savedOrder;
    }

    @Transactional
    public Order sellAsset(Coin coin, Double quantity, User user) throws Exception {
        if (quantity <= 0) {
            throw new Exception("Quantity should be > 0");
        }

        double sellPrice = coin.getCurrentPrice();

        Asset assetToSell = assetService.findAssetByUserIdAndCoinId(user.getId(), coin.getId());

        if (assetToSell == null) {
            throw new Exception("Asset not found");
        }

        double buyPrice =  assetToSell.getBuyPrice();

        OrderItem orderItem = createOrderItem(coin, quantity, buyPrice, sellPrice);

        Order order = createOrder(user, orderItem, OrderType.SELL);
        orderItem.setOrder(order);

        if (assetToSell.getQuantity() < quantity) {
            throw new Exception("Insufficient quantity to sell");
        }

        order.setOrderStatus(OrderStatus.SUCCESS);
        order.setOrderType(OrderType.SELL);
        Order savedOrder = orderRepository.save(order);

        walletService.makeOrderPayment(order, user);

        Asset updatedAsset = assetService.updateAsset(assetToSell.getId(), -quantity);
        if (updatedAsset.getQuantity() * coin.getCurrentPrice() <= 1) {
            assetService.deleteAsset(updatedAsset.getId());
        }

        return savedOrder;
    }

    @Override
    @Transactional
    public Order processOrder(Coin coin, Double quantity, OrderType orderType, User user) throws Exception {
        if (orderType == OrderType.BUY) {
            return buyAsset(coin, quantity, user);
        }
        else if (orderType == OrderType.SELL) {
            return sellAsset(coin, quantity, user);
        }

        throw new Exception("Invalid order type");
    }
}
