package com.cryptoai.traderbot.controller;

import com.cryptoai.traderbot.model.Coin;
import com.cryptoai.traderbot.service.CoinService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coins")
public class CoinController {

    @Autowired
    private CoinService coinService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping
    ResponseEntity<List<Coin>> getCoinList(@RequestParam("page") int page) throws Exception {
        List<Coin> coins = coinService.getCoinList(page);
        return new ResponseEntity<>(coins, HttpStatus.OK);
    }

    @GetMapping("/{coinId}/chart")
    ResponseEntity<JsonNode> getMarketChart(@PathVariable String coinId, @RequestParam("days") int days) throws Exception {
        String res = coinService.getMarketChart(coinId, days);
        JsonNode node = objectMapper.readTree(res);
        return new ResponseEntity<>(node, HttpStatus.OK);
    }

    @GetMapping("/search")
    ResponseEntity<JsonNode> searchCoin(@RequestParam("q") String keyword) throws Exception {
        String res = coinService.searchCoin(keyword);
        JsonNode node = objectMapper.readTree(res);
        return new ResponseEntity<>(node, HttpStatus.OK);
    }

    @GetMapping("/top50")
    ResponseEntity<JsonNode> getTop50CoinsByMarketCapRank() throws Exception {
        String res = coinService.getTop50CoinsByMarketCapRank();
        JsonNode node = objectMapper.readTree(res);
        return new ResponseEntity<>(node, HttpStatus.OK);
    }

    @GetMapping("/trending")
    ResponseEntity<JsonNode> getTrendingCoins() throws Exception {
        String res = coinService.getTrendingCoins();
        JsonNode node = objectMapper.readTree(res);
        return new ResponseEntity<>(node, HttpStatus.OK);
    }

    @GetMapping("/details/{coinId}")
    ResponseEntity<JsonNode> getCoinDetails(@PathVariable String coinId) throws Exception {
        String res = coinService.getCoinDetails(coinId);
        JsonNode node = objectMapper.readTree(res);
        return new ResponseEntity<>(node, HttpStatus.OK);
    }
}
