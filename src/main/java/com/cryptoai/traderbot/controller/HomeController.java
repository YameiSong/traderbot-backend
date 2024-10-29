package com.cryptoai.traderbot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping(value = "/")
    public String home() {
        return "Welcome to Crypto AI Traderbot";
    }

    @GetMapping(value = "/api")
    public String secure() {
        return "Welcome to Crypto AI Traderbot Secure";
    }
}
