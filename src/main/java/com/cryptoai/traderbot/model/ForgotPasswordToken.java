package com.cryptoai.traderbot.model;

import com.cryptoai.traderbot.domain.VerificationType;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class ForgotPasswordToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    @OneToOne
    private User user;

    private String otp;

    private VerificationType verificationType;

    private String sendTo;
}
