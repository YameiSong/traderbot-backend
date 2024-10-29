package com.cryptoai.traderbot.service;

import com.cryptoai.traderbot.model.TwoFactorOTP;
import com.cryptoai.traderbot.model.User;

public interface TwoFactorOtpService {

    TwoFactorOTP createTwoFactorOtp(User user, String otp, String jwt);

    TwoFactorOTP findByUser(Long userId);

    TwoFactorOTP findById(String id);


    /**
     * Verifies the provided one-time password (OTP) for two-factor authentication.
     *
     * @param twoFactorOTP the TwoFactorOTP instance containing the expected OTP and related data
     * @param otp the one-time password provided by the user for verification
     * @return true if the provided OTP matches the expected OTP, false otherwise
     */
    boolean verifyTwoFactorOtp(TwoFactorOTP twoFactorOTP, String otp);

    void deleteTwoFactorOtp(TwoFactorOTP twoFactorOTP);
}
