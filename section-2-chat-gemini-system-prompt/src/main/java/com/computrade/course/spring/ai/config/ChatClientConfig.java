package com.computrade.course.spring.ai.config;


import org.jspecify.annotations.NonNull;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ChatClientConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                //.defaultSystem(getSystemPromptString())
                // Add the built-in logger advisor here
                //.defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }


    private static @NonNull String getSystemPromptString() {
        String systemTemplateText = """
            You are a qualified AI Financial Advisor.
            Tailor your response to the following client parameters:
            - Age: {userAge}
            - Risk Profile: {riskTolerance}
            
            Provide structured, compliant educational insights based on this context.
            """;
        return systemTemplateText;
    }
}
