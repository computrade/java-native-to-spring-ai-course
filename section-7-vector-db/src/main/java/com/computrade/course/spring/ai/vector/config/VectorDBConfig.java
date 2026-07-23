package com.computrade.course.spring.ai.vector.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@Configuration
public class VectorDBConfig {



    @Bean
    public ChatClient chatClient(ChatClient.Builder builder, ChatMemory chatMemory) {

        Advisor loggerAdvisor = new SimpleLoggerAdvisor();
        Advisor messageChatMemoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();

        return builder
               .defaultAdvisors(List.of(loggerAdvisor, messageChatMemoryAdvisor))
                .build();
    }

    @Bean
    public VectorStore pdfVectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel embeddingModel) {
        return PgVectorStore.builder(jdbcTemplate,embeddingModel)
                .vectorTableName("pdf_vector_store")
                .initializeSchema(true)
                .maxDocumentBatchSize(100)
                // all these are default values
                //.indexType(PgVectorStore.PgIndexType.HNSW)
                //.distanceType(PgVectorStore.PgDistanceType.COSINE_DISTANCE)
                //.dimensions(1536) - taken from the embedding model
                //.schemaName("public")
                .build();
    }

}
