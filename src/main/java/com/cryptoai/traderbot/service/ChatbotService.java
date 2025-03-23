package com.cryptoai.traderbot.service;

import com.cryptoai.traderbot.response.ApiResponse;

public interface ChatbotService {
    ApiResponse getCoinDetails(String prompt);

    String simpleChat(String prompt);
}
