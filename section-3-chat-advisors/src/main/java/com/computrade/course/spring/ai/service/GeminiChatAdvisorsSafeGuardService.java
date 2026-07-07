package com.computrade.course.spring.ai.service;

import com.computrade.course.spring.ai.model.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GeminiChatAdvisorsSafeGuardService {

    private final ChatClient chatClientWithSafeGuardAdvisors;
    private final ChatClient chatClientWithCustomSafeGuardAdvisors;
    private final ChatClient chatClientWithRoleGuardAdvisors;


    public GeminiChatAdvisorsSafeGuardService(ChatClient chatClientWithSafeGuardAdvisors, ChatClient chatClientWithCustomSafeGuardAdvisors, ChatClient chatClientWithRoleGuardAdvisors) {
        this.chatClientWithSafeGuardAdvisors = chatClientWithSafeGuardAdvisors;
        this.chatClientWithCustomSafeGuardAdvisors = chatClientWithCustomSafeGuardAdvisors;
        this.chatClientWithRoleGuardAdvisors = chatClientWithRoleGuardAdvisors;
    }

    public String getChatResponseWithSafeGuardAdvisors(String userQuestion) {

        log.info("Calling LLM with User question: {}", userQuestion);

        ChatResponse response = chatClientWithSafeGuardAdvisors.prompt()
                .user(userQuestion)
                .call()
                .chatResponse();

        // Navigate down to get the raw text content:
        assert response != null;
        String resultString = getTextFromChatResponse(response);
        return resultString;

    }

    public String getChatResponseWithCustomSafeGuardAdvisors(String userQuestion) {

        log.info("Calling LLM with Custom Safe Guard and User question: {}", userQuestion);

        ChatResponse response = chatClientWithCustomSafeGuardAdvisors.prompt()
                .user(userQuestion)
                .call()
                .chatResponse();

        // Navigate down to get the raw text content:
        assert response != null;
        String resultString = getTextFromChatResponse(response);
        return resultString;

    }


    public String getChatResponseWithContext(String userQuestion) {

        log.info("Calling LLM with Custom Safe Guard and User question: {}", userQuestion);

        ChatResponse response = chatClientWithRoleGuardAdvisors.prompt()
                .user(userQuestion)
                .advisors(adv -> adv.param("ROLE", UserRole.GUEST))
                .call()
                .chatResponse();

        // Navigate down to get the raw text content:
        assert response != null;
        String resultString = getTextFromChatResponse(response);
        return resultString;

    }

    private static @Nullable String getTextFromChatResponse(ChatResponse response) {
        return response.getResult().getOutput().getText();
    }

}
