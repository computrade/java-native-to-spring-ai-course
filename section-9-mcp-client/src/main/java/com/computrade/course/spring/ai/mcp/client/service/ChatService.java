package com.computrade.course.spring.ai.mcp.client.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChatService {

    private final ChatClient chatClient;

    public String chatWithMcpServer(String prompt) {
        return this.chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

}
