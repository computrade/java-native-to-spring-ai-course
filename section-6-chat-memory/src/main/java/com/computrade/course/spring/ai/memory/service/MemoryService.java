package com.computrade.course.spring.ai.memory.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemoryService {

    private final ChatClient chatClient;

    public String chat(String prompt) {

            String response = chatClient.prompt().user(prompt)
                    // Conversation id must be set to something.
                    .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID,"default"))
                    .call()
                    .content();

            return response;
    }

    public String chat(String userId, String prompt) {

        String response = chatClient.prompt().user(prompt)
                // Conversation id must be set to something.
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, userId))
                .call()
                .content();

        return response;
    }
}
