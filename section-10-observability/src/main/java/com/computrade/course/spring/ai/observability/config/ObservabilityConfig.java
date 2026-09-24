package com.computrade.course.spring.ai.observability.config;


import com.computrade.course.spring.ai.observability.service.StockMarketToolService;
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
import org.springframework.web.client.RestClient;


@Configuration
public class ObservabilityConfig {


    @Bean
    public ChatClient defaultChatClient(ChatClient.Builder builder,
                                        ChatMemory chatMemory,
                                        StockMarketToolService stockMarketToolService) {
        Advisor messageChatMemoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();
        return builder
                .defaultAdvisors(new SimpleLoggerAdvisor(),messageChatMemoryAdvisor)
                .defaultTools(stockMarketToolService)
                .build();
    }

    @Bean
    public VectorStore pdfVectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel embeddingModel) {
        return PgVectorStore.builder(jdbcTemplate,embeddingModel)
                .vectorTableName("pdf_vector_store")
                .initializeSchema(true)
                .maxDocumentBatchSize(100)
                .build();
    }


    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public RestClient finnHubRestClient(RestClient.Builder builder) {
        return builder.baseUrl("https://finnhub.io/api/v1").build();
    }

}
