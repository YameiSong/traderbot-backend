package com.cryptoai.traderbot.service;

import com.cryptoai.traderbot.domain.VerificationType;
import com.cryptoai.traderbot.model.ForgotPasswordToken;
import com.cryptoai.traderbot.model.User;

public interface ForgotPasswordService {

    ForgotPasswordToken createToken(User user, String id, String otp, VerificationType verificationType, String sendTo);

    ForgotPasswordToken findById(String id);

    ForgotPasswordToken findByUser(Long userId);

    void deleteToken(ForgotPasswordToken token);
}
