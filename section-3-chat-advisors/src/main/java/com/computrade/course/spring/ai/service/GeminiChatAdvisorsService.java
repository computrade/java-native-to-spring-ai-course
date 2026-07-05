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
public class GeminiChatAdvisorsService {

    // Matches 'public ChatClient chatClient(...)'
    private final ChatClient defaultChatClient;
    // Matches 'public ChatClient chatClientWithLoggerBuilder(...)'
    private final ChatClient chatClientWithLoggerBuilder;
    // Matches 'public ChatClient chatClientWithLoggerConstructor(...)'
    private final ChatClient chatClientWithLoggerConstructor;


    // 1. Inject the markdown file from the resources folder
    @Value("classpath:prompts/system-prompt.md")
    private Resource systemPromptResource;

    public GeminiChatAdvisorsService(ChatClient defaultChatClient, ChatClient chatClientWithLoggerBuilder, ChatClient chatClientWithLoggerConstructor) {
        this.defaultChatClient = defaultChatClient;
        this.chatClientWithLoggerBuilder = chatClientWithLoggerBuilder;
        this.chatClientWithLoggerConstructor = chatClientWithLoggerConstructor;
    }

    public String getChatResponse(String age, String risk, String userQuestion) {

        // 1. Instantiate the template using the PromptTemplateStringActions interface
        String renderedSystemPrompt = getRenderedSystemPrompt(age, risk);

        // 1. Get the FULL ChatResponse object instead of just the string content
        ChatResponse response = defaultChatClient.prompt()
                .user(userQuestion)
                .system(renderedSystemPrompt)
                .call()
                .chatResponse();

        assert response != null;
        logChatResponse(response);

        // Navigate down to get the raw text content:
        String resultString = getTextFromChatResponse(response);
        return resultString;

    }


    public String getChatResponseWithLoggerBuilder(String age, String risk, String userQuestion) {

        // 1. Instantiate the template using the PromptTemplateStringActions interface
        String renderedSystemPrompt = getRenderedSystemPrompt(age, risk);

        // 1. Get the FULL ChatResponse object instead of just the string content
        ChatResponse response = chatClientWithLoggerBuilder.prompt()
                .user(userQuestion)
                .system(renderedSystemPrompt)
                .call()
                .chatResponse();

        //logChatResponse(response);
        // Navigate down to get the raw text content:
        String resultString = getTextFromChatResponse(response);
        return resultString;

    }


    public String getChatResponseWithLoggerConstructor(String age, String risk, String userQuestion) {

        // 1. Instantiate the template using the PromptTemplateStringActions interface
        String renderedSystemPrompt = getRenderedSystemPrompt(age, risk);

        // 1. Get the FULL ChatResponse object instead of just the string content
        ChatResponse response = chatClientWithLoggerConstructor.prompt()
                .user(userQuestion)
                .system(renderedSystemPrompt)
                .call()
                .chatResponse();

        //logChatResponse(response);

        // Navigate down to get the raw text content:
        String resultString = getTextFromChatResponse(response);
        return resultString;

    }


    private @NonNull String getRenderedSystemPrompt(String age, String risk) {
        PromptTemplateStringActions stringActions = new PromptTemplate(systemPromptResource);

        // 2. Render the template into a plain Java String using a parameters map
        String renderedSystemPrompt = stringActions.render(Map.of(
                "userAge", age,
                "riskTolerance", risk
        ));
        return renderedSystemPrompt;
    }


    private static void logChatResponse(ChatResponse response) {
        // 2. Extract Response Generation Meta (e.g., Finish Reason)
        assert response != null;
        Generation generation = response.getResult();
        assert generation != null;
        ChatGenerationMetadata metadata = generation.getMetadata();
        String finishReason = metadata.getFinishReason();
        log.info("Why the model stopped: {}", finishReason);
        log.info("Rate Limit Info / Usage: {}", response.getMetadata().getUsage());
    }

    private static @Nullable String getTextFromChatResponse(ChatResponse response) {
        return response.getResult().getOutput().getText();
    }

}
