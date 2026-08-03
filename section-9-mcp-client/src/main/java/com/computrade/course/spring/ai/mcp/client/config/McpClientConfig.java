package com.computrade.course.spring.ai.mcp.client.config;


import com.computrade.course.spring.ai.mcp.client.service.StockMarketToolService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;


@Configuration
public class McpClientConfig {


    @Bean
    public ChatClient defaultChatClient(ChatClient.Builder builder,
                                        StockMarketToolService stockMarketToolService) {
        return builder
                .defaultAdvisors(new SimpleLoggerAdvisor())
                //.defaultTools(stockMarketToolService, legacyTaxTool)
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
