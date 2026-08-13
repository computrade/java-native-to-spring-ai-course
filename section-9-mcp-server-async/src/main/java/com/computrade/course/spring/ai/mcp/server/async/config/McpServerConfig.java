package com.computrade.course.spring.ai.mcp.server.async.config;

import com.computrade.course.spring.ai.mcp.server.async.service.StockMarketMcpFeaturesService;
import com.computrade.course.spring.ai.mcp.server.async.service.StockMarketToolService;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.ai.mcp.customizer.McpAsyncServerCustomizer;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;


@Configuration
public class McpServerConfig {

    @Bean
    public ToolCallbackProvider stockTools(StockMarketToolService stockService) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(stockService)
                .build();
    }
    // 2. 🟢 Unified Registration via McpServerCustomizer
    // 2. 🟢 Async Customizer for WebFlux / ASYNC MCP Server
    @Bean
    public McpAsyncServerCustomizer mcpAsyncServerCustomizer(StockMarketMcpFeaturesService featuresService) {
        return serverSpec -> {

            // === A. Register Resource ===
            // === Register Resource as a ResourceTemplate ===
            McpSchema.ResourceTemplate resourceTemplate = McpSchema.ResourceTemplate.builder(
                            "stock://market-summary/{symbol}",
                            "Real-time Stock Summary Resource")
                    .description("Provides text-based stock market metrics for a requested symbol")
                    .mimeType("text/plain")
                    .build();

            // 🟢 Using AsyncResourceSpecification explicitly
            var resourceTemplateSpec = new McpServerFeatures.AsyncResourceTemplateSpecification(
                    resourceTemplate,
                    (exchange, request) -> {
                        String uri = request.uri();
                        String symbol = uri.substring(uri.lastIndexOf('/') + 1);
                        if (symbol.isBlank() || symbol.contains("{")) {
                            symbol = "SPY";
                        }
                        final String targetSymbol = symbol;

                        // 🟢 Offload blocking RestClient call to boundedElastic thread pool
                        return Mono.fromCallable(() -> featuresService.getStockSummaryResource(targetSymbol))
                                .subscribeOn(Schedulers.boundedElastic());

                    }
            );

            serverSpec.resourceTemplates(resourceTemplateSpec);

            // === Register Markdown Documentation Resource ===
            McpSchema.Resource markdownDocResource = McpSchema.Resource.builder(
                            "stock://docs/architecture.md",
                            "Server Architecture Documentation")
                    .description("Provides a Markdown document explaining how this MCP Server works")
                    .mimeType("text/markdown")
                    .build();

            var markdownDocSpec = new McpServerFeatures.AsyncResourceSpecification(
                    markdownDocResource,
                    (exchange, request) -> Mono.just(featuresService.getServerArchitectureDoc())
            );
            serverSpec.resources(markdownDocSpec);

            // === B. Register Prompt ===
            McpSchema.Prompt promptMetaData = McpSchema.Prompt.builder("analyze-stock-portfolio")
                    .description("Generates an AI prompt template for evaluating equity stocks")
                    .arguments(List.of(
                            McpSchema.PromptArgument.builder("symbol")
                                    .required(true)
                                    .description("The stock symbol e.g., AAPL")
                                    .build(),
                            McpSchema.PromptArgument.builder("riskTolerance")
                                    .required(false)
                                    .description("Risk profile: Low, Moderate, High")
                                    .build()
                    ))
                    .build();


            McpSchema.Prompt simplePromptMetaData = McpSchema.Prompt.builder("general-market-overview")
                    .description("Generates a simple high-level market overview prompt")
                    // No arguments added here!
                    .build();

            var simplePromptSpec = new McpServerFeatures.AsyncPromptSpecification(
                    simplePromptMetaData,
                    (exchange, request) -> {
                        // No need to parse request.params().arguments()
                        return Mono.just(featuresService.getGeneralMarketOverviewPrompt());
                    }
            );


            var promptSpec = new McpServerFeatures.AsyncPromptSpecification(
                    promptMetaData,
                    (exchange, request) -> {
                        var args = request.arguments();
                        String symbol = args != null ? (String) args.getOrDefault("symbol", "AAPL") : "AAPL";
                        String risk = args != null ? (String) args.getOrDefault("riskTolerance", "Moderate") : "Moderate";

                        // 🟢 Wrap in Mono.just(...)
                        return Mono.just(featuresService.getStockAnalysisPrompt(symbol, risk));
                    }
            );

            serverSpec.prompts(simplePromptSpec,promptSpec);
        };
    }

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public RestClient finnHubRestClient(RestClient.Builder builder) {
        return builder.baseUrl("https://finnhub.io/api/v1").build();
    }

}
