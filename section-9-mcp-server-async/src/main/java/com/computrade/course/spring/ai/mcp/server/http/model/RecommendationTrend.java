package com.computrade.course.spring.ai.mcp.server.http.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RecommendationTrend(
        @JsonProperty("symbol") String symbol,
        @JsonProperty("period") String period,
        @JsonProperty("strongBuy") int strongBuy,
        @JsonProperty("buy") int buy,
        @JsonProperty("hold") int hold,
        @JsonProperty("sell") int sell,
        @JsonProperty("strongSell") int strongSell
) {
    // Helper method to compute total recommendations
    public int totalAnalysts() {
        return strongBuy + buy + hold + sell + strongSell;
    }
}