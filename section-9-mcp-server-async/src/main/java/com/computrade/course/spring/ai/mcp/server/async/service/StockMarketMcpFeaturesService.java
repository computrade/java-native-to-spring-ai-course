package com.computrade.course.spring.ai.mcp.server.async.service;

import com.computrade.course.spring.ai.mcp.server.async.model.StockQuote;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StockMarketMcpFeaturesService {

    // 🟢 Inject the file directly from src/main/resources/docs/architecture.md
    @Value("classpath:docs/architecture.md")
    private Resource architectureDocResource;

    private final FinnHubStockMarketService finnHubStockMarketService;

    // =========================================================================
    // 1. RESOURCES LOGIC
    // =========================================================================

    /**
     * Provides a dynamically generated market summary resource.
     * URI Format: stock://market-summary/{symbol}
     */
    public McpSchema.ReadResourceResult getStockSummaryResource(String symbol) {
        StockQuote quote = finnHubStockMarketService.getSymbolQuote(symbol);

        String content = """
                === STOCK MARKET SUMMARY RESOURCE ===
                Ticker: %s
                Current Price: $%.2f
                High Price: $%.2f
                Low Price: $%.2f
                Percent Price Change: %.2f
                New York Time: %s
                """.formatted(
                quote.symbol(),
                quote.currentPrice(),
                quote.highPrice(),
                quote.lowPrice(),
                quote.percentPriceChange(),
                quote.getReadableTime()
        );

        McpSchema.TextResourceContents textContents = McpSchema.TextResourceContents
                .builder("stock://market-summary/" + symbol.toLowerCase(),content).build();

        return McpSchema.ReadResourceResult.builder(List.of(textContents)).build();
    }



    // =========================================================================
    // MARKDOWN FILE RESOURCE LOGIC
    // =========================================================================

    /**
     * Reads a Markdown documentation file directly from the classpath/resources directory.
     * URI: stock://docs/architecture.md
     */
    public McpSchema.ReadResourceResult getServerArchitectureDoc() {
        try {
            // Read file content from resources
            String markdownContent = StreamUtils.copyToString(
                    architectureDocResource.getInputStream(),
                    StandardCharsets.UTF_8
            );

            McpSchema.TextResourceContents textContents = McpSchema.TextResourceContents
                    .builder("stock://docs/architecture.md", markdownContent)
                    .mimeType("text/markdown")
                    .build();

            return McpSchema.ReadResourceResult.builder(List.of(textContents)).build();

        } catch (IOException e) {
            throw new RuntimeException("Failed to read resource file from classpath: docs/architecture.md", e);
        }
    }

    // =========================================================================
    // 2. PROMPTS LOGIC
    // =========================================================================

    /**
     * Generates a simple, static prompt template with zero arguments.
     */
    public McpSchema.GetPromptResult getGeneralMarketOverviewPrompt() {
        String content = """
                Act as a Chief Economist. 
                Provide a general, high-level overview of the current global financial stock market trends.
                Highlight key risk factors, interest rate projections, and general sector performance.
                """;

        McpSchema.PromptMessage userMessage = new McpSchema.PromptMessage(
                McpSchema.Role.USER,
                McpSchema.TextContent.builder(content).build()
        );

        return McpSchema.GetPromptResult.builder(List.of(userMessage)).build();
    }


    /**
     * Generates a pre-defined Prompt Template for evaluating stock risk.
     */
    public McpSchema.GetPromptResult getStockAnalysisPrompt(String symbol, String riskTolerance) {
        String template = """
                Act as a Senior Financial Analyst. 
                Please perform a comprehensive stock analysis for ticker: {symbol}.
                Consider that the user's risk tolerance level is: {riskTolerance}.
                
                Please use the available tools (fetchStockPrice, exportCompanyNews) 
                to retrieve real-time market data before formulating your final investment recommendation.
                """;

        PromptTemplate promptTemplate = new PromptTemplate(template);
        Prompt prompt = promptTemplate.create(Map.of(
                "symbol", symbol.toUpperCase(),
                "riskTolerance", riskTolerance != null ? riskTolerance : "Moderate"
        ));

        McpSchema.PromptMessage userMessage = new McpSchema.PromptMessage(
                McpSchema.Role.USER,
                McpSchema.TextContent.builder(prompt.getContents()).build());

        return McpSchema.GetPromptResult.builder(List.of(userMessage)).build();
    }
}
