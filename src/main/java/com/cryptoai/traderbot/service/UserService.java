package com.cryptoai.traderbot.service;

import com.cryptoai.traderbot.domain.VerificationType;
import com.cryptoai.traderbot.model.User;

public interface UserService {

    User findUserByJwt(String jwt) throws Exception;
    User findUserByEmail(String email) throws Exception;
    User findUserById(Long userId) throws Exception;

    User enableTwoFactorAuth(VerificationType verificationType, String sendTo, User user);

    User updatePassword(User user, String newPassword);

}
