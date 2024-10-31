package com.cryptoai.traderbot.request;

import com.cryptoai.traderbot.domain.VerificationType;
import lombok.Data;

@Data
public class ForgotPasswordTokenRequest {
    private VerificationType verificationType;
    private String sendTo;
}
