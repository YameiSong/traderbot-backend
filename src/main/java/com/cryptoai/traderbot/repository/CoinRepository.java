package com.cryptoai.traderbot.repository;

import com.cryptoai.traderbot.model.Coin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoinRepository extends JpaRepository<Coin, String> {
}
