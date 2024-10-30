package com.cryptoai.traderbot.service;

import com.cryptoai.traderbot.domain.VerificationType;
import com.cryptoai.traderbot.model.User;
import com.cryptoai.traderbot.model.VerificationCode;

public interface VerificationCodeService {

    VerificationCode sendVerificationCode(User user, VerificationType verificationType);

    VerificationCode getVerificationCodeById(Long id) throws Exception;

    VerificationCode getVerificationCodeByUser(Long userId);

    void deleteVerificationCode(VerificationCode verificationCode);
}
