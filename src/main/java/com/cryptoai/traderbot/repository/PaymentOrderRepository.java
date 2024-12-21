package com.cryptoai.traderbot.repository;

import com.cryptoai.traderbot.model.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, Long> {
}
