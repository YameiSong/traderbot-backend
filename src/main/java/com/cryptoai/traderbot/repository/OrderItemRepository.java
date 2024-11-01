package com.cryptoai.traderbot.repository;

import com.cryptoai.traderbot.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
