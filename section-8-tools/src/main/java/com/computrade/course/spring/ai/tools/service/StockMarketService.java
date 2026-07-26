package com.computrade.course.spring.ai.tools.service;

import com.computrade.course.spring.ai.tools.model.StockQuote;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Objects;

@Service
public class StockMarketService {

    private final ChatClient chatClient;
    private final RestClient restClient;

    @Value("${app.finnhub.api-key}")
    String apiKey;

    // Injecting the RestClient builder and the token from application.yml
    public StockMarketService(RestClient.Builder builder,
                              ChatClient chatClient) {
        this.chatClient = chatClient;
        this.restClient = builder
                .baseUrl("https://finnhub.io/api/v1")
                .build();
    }

    @Tool(
            name = "fetchStockPrice",
            description = "Retrieves the latest real-time stock quote metrics (current, high, low) for a given equity ticker symbol."
    )
    public StockQuote getLatestQuote(
            @ToolParam(description = "The uppercase stock exchange ticker symbol, for example 'AAPL', 'GOOG', 'TSLA'.") String ticker
    ) {
        // Querying the free Finnhub /quote endpoint
        StockQuote response = Objects.requireNonNull(this.restClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/quote")
                                .queryParam("symbol", ticker.toUpperCase())
                                .queryParam("token", apiKey)
                                .build())
                        .retrieve()
                        .body(StockQuote.class));


        // Fail fast if the API returns an empty payload or missing data key
        if (response.currentPrice() == 0) {
            throw new RuntimeException("Finnhub failed to return valid data for ticker: " + ticker);
        }

        // 2. Leverage our immutable wither pattern to map the uppercase ticker and return the payload
        return response.withTicker(ticker.toUpperCase());

    }

}
