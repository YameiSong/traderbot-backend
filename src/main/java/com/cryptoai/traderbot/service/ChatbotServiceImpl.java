package com.cryptoai.traderbot.service;

import com.cryptoai.traderbot.response.ApiResponse;
import org.springframework.stereotype.Service;

@Service
public class ChatbotServiceImpl implements ChatbotService {
    @Override
    public ApiResponse getCoinDetails(String prompt) {
        return null;
    }

    @Override
    public String simpleChat(String prompt) {
        return "";
    }
}
