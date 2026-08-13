package com.computrade.course.spring.ai.mcp.server.config;


import com.computrade.course.spring.ai.mcp.server.service.StockMarketMcpPromptService;
import com.computrade.course.spring.ai.mcp.server.service.StockMarketMcpResourceService;
import com.computrade.course.spring.ai.mcp.server.service.StockMarketToolService;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.ai.mcp.customizer.McpAsyncServerCustomizer;
import org.springframework.ai.mcp.customizer.McpSyncServerCustomizer;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import reactor.core.publisher.Mono;

import java.util.List;


@Configuration
public class McpServerConfig {

    @Bean
    public ToolCallbackProvider stockTools(StockMarketToolService stockService) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(stockService)
                .build();
    }

    @Bean
    public McpSyncServerCustomizer mcpSyncServerCustomizer(StockMarketMcpResourceService resourceService,
                                                           StockMarketMcpPromptService promptService) {
        return serverSpec -> {
            handleResource(resourceService, serverSpec);
            handlePrompt(promptService, serverSpec);

        };
    }


    private static void handlePrompt(StockMarketMcpPromptService promptService, McpServer.SyncSpecification<?> serverSpec) {
        McpSchema.Prompt simplePromptMetaData = McpSchema.Prompt.builder("general-stock-evaluation")
                .description("Generates a simple high-level stock evaluation prompt")
                // No arguments added here!
                .build();

        var simplePromptSpec = new McpServerFeatures.SyncPromptSpecification(
                simplePromptMetaData,
                (exchange, request) -> {
                    // No need to parse request.params().arguments()
                    return promptService.getGeneralMarketOverviewPrompt();
                }
        );
        serverSpec.prompts(simplePromptSpec);
    }

    private static void handleResource(StockMarketMcpResourceService resourceService, McpServer.SyncSpecification<?> serverSpec) {
        // === Register Markdown Documentation Resource ===
        McpSchema.Resource markdownDocResource = McpSchema.Resource.builder(
                        "stock://docs/architecture.md",
                        "Server Architecture Documentation")
                .description("Provides a Markdown document explaining how this MCP Server works")
                .mimeType("text/markdown")
                .build();

        var markdownDocSpec = new McpServerFeatures.SyncResourceSpecification(
                markdownDocResource,
                (exchange, request) -> resourceService.getServerArchitectureDoc()
        );
        serverSpec.resources(markdownDocSpec);
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
