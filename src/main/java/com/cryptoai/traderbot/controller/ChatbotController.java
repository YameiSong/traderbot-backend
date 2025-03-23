package com.cryptoai.traderbot.controller;

import com.cryptoai.traderbot.model.PromptBody;
import com.cryptoai.traderbot.response.ApiResponse;
import com.cryptoai.traderbot.service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    public ResponseEntity<ApiResponse> getCoinDetails(@RequestBody PromptBody prompt) {
        ApiResponse response = new ApiResponse();
        response.setMessage(prompt.getPrompt());
        return ResponseEntity.ok(response);
    }
}
