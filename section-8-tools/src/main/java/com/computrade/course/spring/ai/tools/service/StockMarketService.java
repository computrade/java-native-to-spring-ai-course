package com.computrade.course.spring.ai.tools.service;

import com.computrade.course.spring.ai.tools.model.StockQuote;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StockMarketService {

    private final RestClient finnHubRestClient;

    @Value("${app.finnhub.api-key}")
    String apiKey;


    @Tool(
            name = "fetchStockPrice",
            description = "Retrieves the latest real-time stock quote metrics (current, high, low) for a given equity ticker symbol."
    )
    public StockQuote fetchStockPrice(
            @ToolParam(description = "The uppercase stock exchange ticker symbol, for example 'AAPL', 'GOOG', 'TSLA'.") String symbol
    ) {
        // Querying the free Finnhub /quote endpoint
        StockQuote response = Objects.requireNonNull(finnHubRestClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/quote")
                                .queryParam("symbol", symbol.toUpperCase())
                                .queryParam("token", apiKey)
                                .build())
                        .retrieve()
                        .body(StockQuote.class));


        // Fail fast if the API returns an empty payload or missing data key
        if (response.currentPrice() == 0) {
            throw new RuntimeException("Finnhub failed to return valid data for symbol: " + symbol);
        }

        // 2. Leverage our immutable wither pattern to map the uppercase ticker and return the payload
        return response.withTicker(symbol.toUpperCase());

    }

}
