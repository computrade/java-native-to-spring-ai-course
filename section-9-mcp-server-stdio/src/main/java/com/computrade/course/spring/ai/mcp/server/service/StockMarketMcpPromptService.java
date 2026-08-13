package com.computrade.course.spring.ai.mcp.server.service;

import io.modelcontextprotocol.spec.McpSchema;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StockMarketMcpPromptService {

    /**
     * Generates a simple, static prompt template with zero arguments.
     */
    public McpSchema.GetPromptResult getGeneralMarketOverviewPrompt() {
        String content = """
                Act as a Senior Stock Valuation Analyst.
                Provide a rigorous valuation framework and assessment for evaluating equity stocks.
                Focus on key financial metrics, cash flow projections (DCF analysis), valuation multiples (P/E, EV/EBITDA, P/S), 
                and determining intrinsic value versus market price with an adequate Margin of Safety.
                """;

        McpSchema.PromptMessage userMessage = new McpSchema.PromptMessage(
                McpSchema.Role.USER,
                McpSchema.TextContent.builder(content).build()
        );

        return McpSchema.GetPromptResult.builder(List.of(userMessage)).build();
    }


}
