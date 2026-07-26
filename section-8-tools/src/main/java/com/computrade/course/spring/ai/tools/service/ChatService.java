package com.computrade.course.spring.ai.tools.service;

import com.computrade.course.spring.ai.tools.model.StockQuote;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class ChatService {

    private final ChatClient chatClient;
    private final StockMarketService stockMarketService;


    public String chatWithQuoteTool(String prompt) {
        return this.chatClient.prompt()
                .user(prompt)
                .tools(stockMarketService) // <-- Dynamic Per-Request Tool Scope!
                .call()
                .content();
    }

}
