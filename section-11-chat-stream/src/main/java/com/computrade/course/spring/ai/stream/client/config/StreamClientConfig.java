package com.computrade.course.spring.ai.stream.client.config;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class StreamClientConfig {


    @Bean
    public ChatClient defaultChatClient(ChatClient.Builder builder){

        return builder.build();
    }

}
