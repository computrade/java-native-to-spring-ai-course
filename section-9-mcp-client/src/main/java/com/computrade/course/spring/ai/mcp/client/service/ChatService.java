package com.computrade.course.spring.ai.mcp.client.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChatService {

    private final ChatClient chatClient;
    private final StockMarketToolService stockMarketToolService;

    public String chatWithQuoteTool(String prompt) {
        return this.chatClient.prompt()
                .user(prompt)
                .tools(stockMarketToolService) // <-- Dynamic Per-Request Tool Scope!
                .call()
                .content();
    }

}
