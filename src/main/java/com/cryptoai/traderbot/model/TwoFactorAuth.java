package com.cryptoai.traderbot.model;

import com.cryptoai.traderbot.domain.VerificationType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TwoFactorAuth {

    private boolean enabled = false;

    private VerificationType verificationType;

    private String sendTo;
}
