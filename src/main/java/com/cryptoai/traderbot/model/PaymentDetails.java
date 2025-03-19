package com.cryptoai.traderbot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class PaymentDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonProperty("accountNumber")
    private String accountNumber;

    @JsonProperty("accountHolderName")
    private String accountHolderName;

    @JsonProperty("ifsc")
    private String ifsc;

    @JsonProperty("bankName")
    private String bankName;

    @OneToOne
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private User user;
}
