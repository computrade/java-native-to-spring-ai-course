package com.computrade.course.spring.ai.service;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.ChatGenerationMetadata;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.PromptTemplateStringActions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class GeminiChatAdvisorsSafeGuardService {

    private final ChatClient chatClientWithSafeGuardAdvisors;
    private final ChatClient chatClientWithCustomSafeGuardAdvisors;

    public GeminiChatAdvisorsSafeGuardService(ChatClient chatClientWithSafeGuardAdvisors, ChatClient chatClientWithCustomSafeGuardAdvisors) {
        this.chatClientWithSafeGuardAdvisors = chatClientWithSafeGuardAdvisors;
        this.chatClientWithCustomSafeGuardAdvisors = chatClientWithCustomSafeGuardAdvisors;
    }

    public String getChatResponseWithSafeGuardAdvisors(String userQuestion) {

        ChatResponse response = chatClientWithSafeGuardAdvisors.prompt()
                .user(userQuestion)
                .call()
                .chatResponse();

        // Navigate down to get the raw text content:
        String resultString = getTextFromChatResponse(response);
        return resultString;

    }

    public String getChatResponseWithCustomSafeGuardAdvisors(String userQuestion) {

        ChatResponse response = chatClientWithCustomSafeGuardAdvisors.prompt()
                .user(userQuestion)
                .call()
                .chatResponse();

        // Navigate down to get the raw text content:
        String resultString = getTextFromChatResponse(response);
        return resultString;

    }

    private static @Nullable String getTextFromChatResponse(ChatResponse response) {
        return response.getResult().getOutput().getText();
    }

}
