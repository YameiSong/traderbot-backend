package com.cryptoai.traderbot.service;

import com.cryptoai.traderbot.domain.VerificationType;
import com.cryptoai.traderbot.model.User;
import com.cryptoai.traderbot.model.VerificationCode;
import com.cryptoai.traderbot.repository.VerificationCodeRepository;
import com.cryptoai.traderbot.utils.OtpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VerificationCodeServiceImpl implements VerificationCodeService{

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;

    @Override
    public VerificationCode sendVerificationCode(User user, VerificationType verificationType) {
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setUser(user);
        verificationCode.setOtp(OtpUtils.generateOtp());
        verificationCode.setVerificationType(verificationType);
        return verificationCodeRepository.save(verificationCode);
    }

    @Override
    public VerificationCode getVerificationCodeById(Long id) throws Exception {
        Optional<VerificationCode> verificationCode = verificationCodeRepository.findById(id);
        if (verificationCode.isPresent()) {
            return verificationCode.get();
        }
        throw new Exception("Verification code not found");
    }

    @Override
    public VerificationCode getVerificationCodeByUser(Long userId) {
        return verificationCodeRepository.findByUserId(userId);
    }

    @Override
    public void deleteVerificationCode(VerificationCode verificationCode) {
        verificationCodeRepository.delete(verificationCode);
    }
}
