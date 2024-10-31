package com.cryptoai.traderbot.service;

import com.cryptoai.traderbot.domain.VerificationType;
import com.cryptoai.traderbot.model.ForgotPasswordToken;
import com.cryptoai.traderbot.model.User;
import com.cryptoai.traderbot.repository.ForgotPasswordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ForgotPasswordServiceImpl implements ForgotPasswordService {

    @Autowired
    private ForgotPasswordRepository forgotPasswordRepository;

    @Override
    public ForgotPasswordToken createToken(User user, String id, String otp, VerificationType verificationType, String sendTo) {
        ForgotPasswordToken token = new ForgotPasswordToken();
        token.setUser(user);
        token.setId(id);
        token.setOtp(otp);
        token.setVerificationType(verificationType);
        token.setSendTo(sendTo);
        return forgotPasswordRepository.save(token);
    }

    @Override
    public ForgotPasswordToken findById(String id) {
        return forgotPasswordRepository.findById(id).orElse(null);
    }

    @Override
    public ForgotPasswordToken findByUser(Long userId) {
        return forgotPasswordRepository.findByUserId(userId);
    }

    @Override
    public void deleteToken(ForgotPasswordToken token) {
        forgotPasswordRepository.delete(token);
    }
}
