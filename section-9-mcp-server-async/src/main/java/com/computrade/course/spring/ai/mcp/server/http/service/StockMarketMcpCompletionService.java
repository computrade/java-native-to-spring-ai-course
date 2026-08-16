package com.computrade.course.spring.ai.mcp.server.http.service;

import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockMarketMcpCompletionService {

    // Predefined ticker list for autocompletion suggestions
    private static final List<String> POPULAR_TICKERS = List.of(
            "AAPL", "AMZN", "GOOGL", "MSFT", "NVDA", "SPY", "TSLA"
    );

    /**
     * Generates autocompletion suggestions for resource template URIs or prompt arguments.
     * URI Reference: stock://market-summary/{symbol}
     *
     * @param prefix The user's partial input string typed in the MCP Inspector/Client.
     * @return McpSchema.CompleteResult containing the matching ticker suggestions.
     */
    public McpSchema.CompleteResult getSymbolCompletions(String prefix) {
        String cleanPrefix = (prefix != null) ? prefix.trim().toUpperCase() : "";

        List<String> suggestions = POPULAR_TICKERS.stream()
                .filter(ticker -> ticker.startsWith(cleanPrefix))
                .toList();

        return new McpSchema.CompleteResult(
                new McpSchema.CompleteResult.CompleteCompletion(
                        suggestions,
                        suggestions.size(),
                        false // total is complete (no further pages)
                )
        );
    }
}