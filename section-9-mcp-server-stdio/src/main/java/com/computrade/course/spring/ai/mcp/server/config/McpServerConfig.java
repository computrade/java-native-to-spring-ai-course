package com.computrade.course.spring.ai.mcp.server.config;


import com.computrade.course.spring.ai.mcp.server.service.StockMarketToolService;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

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
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public RestClient finnHubRestClient(RestClient.Builder builder) {
        return builder.baseUrl("https://finnhub.io/api/v1").build();
    }

}
