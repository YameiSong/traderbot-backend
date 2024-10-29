package com.cryptoai.traderbot.model;

import com.cryptoai.traderbot.domain.VerificationType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TwoFactorAuth {
    @JsonProperty("enabled")
    private boolean enabled = false;

    @JsonProperty("sendTo")
    private VerificationType sendTo;
}
