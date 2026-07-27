package com.computrade.course.spring.ai.tools.service;

import com.computrade.course.spring.ai.tools.model.CompanyNews;
import com.computrade.course.spring.ai.tools.model.StockQuote;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
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

    @Tool(
            name = "exportCompanyNews",
            description = "Downloads and exports the raw recent company news articles for a given ticker symbol.",
            returnDirect = true // Snaps the LLM loop and delivers the raw collection directly to the controller
    )
    public List<CompanyNews> exportCompanyNews(
            @ToolParam(description = "The uppercase equity ticker symbol, e.g. 'TSLA', 'MSFT'") String ticker
    ) {
        // 1. Calculate the dynamic 30-day date range automatically using local Java time
        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysAgo = today.minusDays(30);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String toDate = today.format(formatter);
        String fromDate = thirtyDaysAgo.format(formatter);

        // 2. Fetch the modern collection payload directly using ParameterizedTypeReference
        List<CompanyNews> newsList = finnHubRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/company-news")
                        .queryParam("symbol", ticker.toUpperCase())
                        .queryParam("from", fromDate)
                        .queryParam("to", toDate)
                        .queryParam("token", apiKey)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<List<CompanyNews>>() {
                });

        if (newsList == null || newsList.isEmpty()) {
            throw new RuntimeException("No recent news found for ticker: " + ticker);
        }

        // 3. Return the limited stream of structural POJOs directly to the response pipeline
        return newsList.stream().limit(5).toList();
    }
}



