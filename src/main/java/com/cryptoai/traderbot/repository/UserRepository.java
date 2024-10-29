package com.cryptoai.traderbot.repository;

import com.cryptoai.traderbot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
