package com.cryptoai.traderbot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
public class Coin {

    @Id
    private String id;

    private String symbol;
    private String name;

    @Column(length = 512)
    private String image;

    private Double currentPrice;
    private Long marketCap;
    private Integer marketCapRank;
    private Long fullyDilutedValuation;
    private Long totalVolume;
    private Double high24h;
    private Double low24h;
    private Double priceChange24h;
    private Double priceChangePercentage24h;
    private Long marketCapChange24h;
    private Long marketCapChangePercentage24h;
    private Double circulatingSupply;
    private Long totalSupply;
    private Long maxSupply;
    private Double ath;
    private Double athChangePercentage;
    private LocalDateTime athDate;
    private Double atl;
    private Double atlChangePercentage;
    private LocalDateTime atlDate;
    private Double roi;
    private LocalDateTime lastUpdated;
}