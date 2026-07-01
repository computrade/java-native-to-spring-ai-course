package com.computrade.course.spring.ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.PromptTemplateStringActions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor // Lombok automatically generates the constructor for the final field below
public class GeminiAdvancedChatService {

    // Direct, immutable injection of our centralized bean
    private final ChatClient chatClient;

    // 1. Inject the markdown file from the resources folder
    @Value("classpath:prompts/system-prompt.md")
    private Resource systemPromptResource;

    public String getChatResponse(String age, String risk, String userQuestion) {

        // 1. Instantiate the template using the PromptTemplateStringActions interface
        String renderedSystemPrompt = getRenderedSystemPrompt(age, risk);

        // 1. Get the FULL ChatResponse object instead of just the string content
        ChatResponse response = chatClient.prompt()
                .user(userQuestion)
                .system(renderedSystemPrompt)
                .call()
                .chatResponse();

        // 2. Extract Response Generation Meta (e.g., Finish Reason)
        Generation generation = response.getResult();
        String finishReason = generation.getMetadata().getFinishReason();
        log.info("Why the model stopped: {}", finishReason);


        if (response.getMetadata() != null) {
            log.info("Rate Limit Info / Usage: {}", response.getMetadata().getUsage());
        }


        // Navigate down to get the raw text content:
        String resultString = response.getResult().getOutput().getText();
        return resultString;

    }




    public String getChatResponseLocalOptions(String age, String risk,String userQuestion) {

        // 1. Instantiate the template using the PromptTemplateStringActions interface
        String renderedSystemPrompt = getRenderedSystemPrompt(age, risk);

        // 1. Get the FULL ChatResponse object instead of just the string content
        ChatResponse response = chatClient.prompt()
                .user(userQuestion)
                .system(renderedSystemPrompt)
                .options(ChatOptions.builder().temperature(0.9).maxTokens(1000))
                .call()
                .chatResponse();

        // 2. Extract Response Generation Meta (e.g., Finish Reason)
        Generation generation = response.getResult();
        String finishReason = generation.getMetadata().getFinishReason();
        log.info("Why the model stopped: {}", finishReason);


        if (response.getMetadata() != null) {
            log.info("Rate Limit Info / Usage: {}", response.getMetadata().getUsage());
        }

        // Navigate down to get the raw text content:
        String resultString = response.getResult().getOutput().getText();
        return resultString;

    }


    public String getChatResponseWithConfigs(double temperature, Integer maxToken,String userQuestion) {

        // 1. Get the FULL ChatResponse object instead of just the string content
        ChatResponse response = chatClient.prompt()
                .user(userQuestion)
                .options(ChatOptions.builder().

                        temperature(temperature).maxTokens(maxToken))
                .call()
                .chatResponse();

        // 2. Extract Response Generation Meta (e.g., Finish Reason)
        Generation generation = response.getResult();
        String finishReason = generation.getMetadata().getFinishReason();
        log.info("Why the model stopped: {}", finishReason);


        if (response.getMetadata() != null) {
            log.info("Rate Limit Info / Usage: {}", response.getMetadata().getUsage());
        }

        // Navigate down to get the raw text content:
        String resultString = response.getResult().getOutput().getText();
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

}
