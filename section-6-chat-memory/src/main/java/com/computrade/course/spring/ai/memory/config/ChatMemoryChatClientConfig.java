package com.computrade.course.spring.ai.memory.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ChatMemoryChatClientConfig {


//    @Autowired
//    JdbcChatMemoryRepository chatMemoryRepository;

//    @Bean
//    public ChatMemoryRepository chatMemoryRepository() {
//        return new InMemoryChatMemoryRepository();
//    }

//    @Bean
//    public ChatMemoryRepository chatMemoryRepository(JdbcTemplate jdbcTemplate) {
//        return JdbcChatMemoryRepository.builder()
//                .jdbcTemplate(jdbcTemplate)
//                .dialect(new PostgresChatMemoryRepositoryDialect())
//                .build();
//    }

//    @Bean
//    public ChatMemory chatMemory(ChatMemoryRepository chatMemoryRepository) {
//        return MessageWindowChatMemory.builder()
//                .chatMemoryRepository(chatMemoryRepository)
//                .maxMessages(3)
//                .build();
//    }


    @Bean
    public ChatClient chatClient(ChatClient.Builder builder, ChatMemory chatMemory) {

        Advisor loggerAdvisor = new SimpleLoggerAdvisor();
        Advisor messageChatMemoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();

        return builder
                .defaultAdvisors(List.of(loggerAdvisor, messageChatMemoryAdvisor))
                .build();
    }
}
