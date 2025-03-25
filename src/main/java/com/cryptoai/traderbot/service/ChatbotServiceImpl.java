package com.cryptoai.traderbot.service;

import com.cryptoai.traderbot.response.ApiResponse;
import com.cryptoai.traderbot.response.FunctionResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ChatbotServiceImpl implements ChatbotService {
    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Autowired
    private CoinService coinService;

    @Override
    public String getCoinDetails(String prompt) throws Exception {
        String geminiApiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + geminiApiKey;

        FunctionResponse functionResponse = getFunctionResponse(prompt);
        String apiResponse = coinService.getCoinDetails(functionResponse.getCurrencyName().toLowerCase());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject requestBody = new JSONObject()
                .put("contents", new JSONArray()
                        .put(new JSONObject()
                                .put("role", "user")
                                .put("parts", new JSONArray()
                                        .put(new JSONObject()
                                                .put("text", prompt)
                                        )
                                )
                        )
                        .put(new JSONObject()
                                .put("role", "model")
                                .put("parts", new JSONArray()
                                        .put(new JSONObject()
                                                .put("functionCall", new JSONObject()
                                                        .put("name", "getCoinDetails")
                                                        .put("args", new JSONObject()
                                                                .put("currencyName", functionResponse.getCurrencyName())
                                                                .put("currencyData", functionResponse.getCurrencyData())
                                                        )
                                                )
                                        )
                                )
                        )
                        .put(new JSONObject()
                                .put("role", "user")
                                .put("parts", new JSONArray()
                                        .put(new JSONObject()
                                                .put("functionResponse", new JSONObject()
                                                        .put("name", "getCoinDetails")
                                                        .put("response", new JSONObject()
                                                                .put("name", "getCoinDetails")
                                                                .put("content", apiResponse)
                                                        )
                                                )
                                        )
                                )
                        )
                )
                .put("tools", new JSONArray().put(
                        new JSONObject().put(
                                "functionDeclarations", new JSONArray().put(
                                        new JSONObject()
                                                .put("name", "getCoinDetails")
                                                .put("description", "Get the coin details from a given currency object")
                                                .put("parameters", new JSONObject()
                                                        .put("type", "object")
                                                        .put("properties", new JSONObject()
                                                                .put("currencyName", new JSONObject()
                                                                        .put("type", "string")
                                                                        .put("description", "The currency name, id, or symbol"))
                                                                .put("currencyData", new JSONObject()
                                                                        .put("type", "string")
                                                                        .put("description", "Detailed data about the currency, including price, market cap, etc.")
                                                                )
                                                        )
                                                        .put("required", new JSONArray()
                                                                .put("currencyName")
                                                                .put("currencyData")
                                                        )
                                                )
                                )
                        )
                ));

        HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);
        RestTemplate restTemplate = new RestTemplate();

        String response = restTemplate.postForObject(geminiApiUrl, request, String.class);

        JSONObject jsonObject = new JSONObject(response);

        JSONArray candidates = jsonObject.getJSONArray("candidates");
        JSONObject firstCandidate = candidates.getJSONObject(0);

        JSONObject content = firstCandidate.getJSONObject("content");
        JSONArray parts = content.getJSONArray("parts");
        JSONObject firstPart = parts.getJSONObject(0);
        String text = firstPart.getString("text");

        return text;
    }


    public FunctionResponse getFunctionResponse(String prompt) {
        String geminiApiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + geminiApiKey;

        JSONObject requestBody = new JSONObject()
                .put("contents", new JSONArray()
                        .put(new JSONObject()
                                .put("role", "user")
                                .put("parts", new JSONArray()
                                        .put(new JSONObject()
                                                .put("text", prompt)
                                        )
                                )
                        )
                )
                .put("tools", new JSONArray().put(
                        new JSONObject().put(
                                "functionDeclarations", new JSONArray().put(
                                        new JSONObject()
                                                .put("name", "getCoinDetails")
                                                .put("description", "Get the coin details from given currency object")
                                                .put("parameters", new JSONObject()
                                                        .put("type", "object")
                                                        .put("properties", new JSONObject()
                                                                .put("currencyName", new JSONObject()
                                                                        .put("type", "string")
                                                                        .put("description", "The currency name, id, or symbol"))
                                                                .put("currencyData", new JSONObject()
                                                                        .put("type", "string")
                                                                        .put("description", "Currency Data, can be id, symbol, name, image, current_price, market_cap, market_cap_rank, fully_diluted_valuation, total_volume, high_24h, low_24h, price_change_24h, price_change_percentage_24h, market_cap_change_24h, market_cap_change_percentage_24h, circulating_supply, total_supply, max_supply, ath, ath_change_percentage, ath_date, atl, atl_change_percentage, atl_date, last_updated.")))
                                                        .put("required", new JSONArray()
                                                                .put("currencyName")
                                                                .put("currencyData")))
                                )
                        )
                )
        )
        .put("toolConfig", new JSONObject()
                .put("functionCallingConfig", new JSONObject()
                        .put("mode", "ANY")
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.postForObject(geminiApiUrl, request, String.class);

        JSONObject jsonObject = new JSONObject(response);

        JSONArray candidates = jsonObject.getJSONArray("candidates");
        JSONObject firstCandidate = candidates.getJSONObject(0);

        JSONObject content = firstCandidate.getJSONObject("content");
        JSONArray parts = content.getJSONArray("parts");
        JSONObject firstPart = parts.getJSONObject(0);
        JSONObject functionCall = firstPart.getJSONObject("functionCall");

        String functionName = functionCall.getString("name");
        JSONObject args = functionCall.getJSONObject("args");
        String currencyName = args.getString("currencyName");
        String currencyData = args.getString("currencyData");

        FunctionResponse functionResponse = new FunctionResponse();
        functionResponse.setFunctionName(functionName);
        functionResponse.setCurrencyName(currencyName);
        functionResponse.setCurrencyData(currencyData);

        return functionResponse;
    }
}
