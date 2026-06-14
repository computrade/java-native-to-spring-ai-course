package com.computrade.course.spring.ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor // Lombok automatically generates the constructor for the final field below
public class GeminiChatService {

    // Direct, immutable injection of our centralized bean
    private final ChatClient chatClient;

    /**
     * Executes a standard blocking call to Gemini
     */
    public String getChatResponse(String prompt) {
        return chatClient.prompt(prompt)
                .call()
                .content();
    }


}
