package com.computrade.course.spring.ai.tools.service;

import com.computrade.course.spring.ai.tools.model.Tenant;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class ChatService {

    private final ChatClient chatClient;
    private final StockMarketToolService stockMarketToolService;
    private final StockMarketSecureToolService stockMarketSecureToolService;

    public String chatWithQuoteTool(String prompt) {
        return this.chatClient.prompt()
                .user(prompt)
                .tools(stockMarketToolService) // <-- Dynamic Per-Request Tool Scope!
                .call()
                .content();
    }

    public String chatWithQuoteToolCotext(String prompt, Tenant tenant) {

        return this.chatClient.prompt()
                .user(prompt)
                .tools(stockMarketSecureToolService)
                .toolContext(Map.of("tenantId", tenant.getId()))
                .call()
                .content();
    }
}
