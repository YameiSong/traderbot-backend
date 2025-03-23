package com.cryptoai.traderbot.service;

import com.cryptoai.traderbot.response.ApiResponse;

public interface ChatbotService {
    String getCoinDetails(String prompt) throws Exception;
}
