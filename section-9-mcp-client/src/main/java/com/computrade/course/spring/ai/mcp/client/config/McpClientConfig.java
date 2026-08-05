package com.computrade.course.spring.ai.mcp.client.config;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.client.RestClient;


@Configuration
public class McpClientConfig {


    @Value("classpath:prompts/system-prompt.md")
    private Resource systemPromptResource;

    @Bean
    public ChatClient defaultChatClient(ChatClient.Builder builder,
                                        ToolCallbackProvider toolCallbackProvider) {
        return builder
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .defaultSystem(systemPromptResource)
                .defaultTools(toolCallbackProvider)
                .build();
    }

}
