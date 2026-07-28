package com.computrade.course.spring.ai.tools.config;


import com.computrade.course.spring.ai.tools.model.TaxRequest;
import com.computrade.course.spring.ai.tools.service.LegacyTaxCalculator;
import com.computrade.course.spring.ai.tools.service.StockMarketToolService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.method.MethodToolCallback;
import org.springframework.ai.tool.support.ToolDefinitions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.client.RestClient;

import java.lang.reflect.Method;


@Configuration
public class ToolsConfig {


    @Bean
    public ChatClient defaultChatClient(ChatClient.Builder builder,
                                        StockMarketToolService stockMarketToolService,
                                        ToolCallback legacyTaxTool) {

        return builder
                .defaultAdvisors(new SimpleLoggerAdvisor())
                //.defaultTools(stockMarketToolService, legacyTaxTool)
                .build();
    }

    // Programmatically registering a legacy method
    @Bean
    public ToolCallback legacyTaxTool(LegacyTaxCalculator legacyCalculator) {
        // Safe programmatic lookup of the target legacy method using Spring's ReflectionUtils
        Method method = ReflectionUtils.findMethod(LegacyTaxCalculator.class, "calculatePurchaseTax", TaxRequest.class);

        if (method == null) {
            throw new IllegalStateException("Failed to find target legacy method: calculatePurchaseTax");
        }

        // Building the modern ToolCallback with its descriptive metadata schema
        return MethodToolCallback.builder()
                .toolDefinition(ToolDefinitions.builder(method)
                        .name("calculateStockTax") // Custom overriding name for the LLM
                        .description("Calculates the regional tax rate for a specific stock ticker based on the country code.")
                        .build())
                .toolObject(legacyCalculator) // Pass the object instance here
                .toolMethod(method)            // Pass the method object here
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
