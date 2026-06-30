package com.computrade.course.spring.ai.config;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ChatClientConfigAdvanced {


    @Bean
    public ChatOptions applicationDefaultOptions() {
        return ChatOptions.builder().temperature(0.5).maxTokens(200).stopSequences(List.of("##STOP")).build();
    }

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder,
                                 ChatOptions chatOptions) {

        return builder
                .defaultOptions(chatOptions.mutate())
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }
}
