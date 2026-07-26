package com.computrade.course.spring.ai.tools.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public record StockQuote(
        String symbol,
        @JsonProperty("c") double currentPrice,
        @JsonProperty("h") double highPrice,
        @JsonProperty("l") double lowPrice,
        @JsonProperty("t") long timestamp
) {
    // A fluent "wither" method to attach the ticker seamlessly
    public StockQuote withTicker(String tickerSymbol) {
        return new StockQuote(tickerSymbol, this.currentPrice, this.highPrice, this.lowPrice, this.timestamp);
    }

    // Derived JSON property helper allowing the LLM to easily read human time
    @JsonProperty("readableTime")
    public String getReadableTime() {
        if (this.timestamp == 0) return "N/A";
        return Instant.ofEpochSecond(this.timestamp)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
