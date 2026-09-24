package com.computrade.course.spring.ai.observability.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VectorDBService {

    String SOURCE_KEY_WORD = "source";

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    @Value("classpath:systemPromptForCourse.st")
    private Resource courseSystemPrompt;

    @Value("classpath:data/courses_dataset.csv")
    private Resource csvResource;


    // Filtered Semantic Search
    public String queryAllCourse(String convId, String prompt) {

        FilterExpressionBuilder filterBuilder = new FilterExpressionBuilder();
        Filter.Expression filterExpression = filterBuilder
                .eq(SOURCE_KEY_WORD, Objects.requireNonNull(csvResource.getFilename()))
                .build();

        SearchRequest searchRequest = SearchRequest.builder()
                .query(prompt)
                .topK(3)
                .filterExpression(filterExpression) // Filter magic happens here.
                .build();


        return getChatResponse(convId, prompt, searchRequest);
    }


    @Nullable
    private String getChatResponse(String convId, String prompt, SearchRequest searchRequest) {
        List<Document> similarDocs = vectorStore.similaritySearch(searchRequest);
        String similarDocsContent = similarDocs.stream()
                .map(Document::getText)
                .collect(Collectors.joining(System.lineSeparator()));

        String response = chatClient.prompt()
                .system(promptSystemSpec -> promptSystemSpec.text(courseSystemPrompt).param("courses", similarDocsContent))
                .user(prompt)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, convId))
                .call()
                .content();

        return response;
    }
}
