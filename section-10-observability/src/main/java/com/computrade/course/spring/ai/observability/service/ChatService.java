package com.computrade.course.spring.ai.observability.service;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ChatService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    @Value("classpath:systemPromptForCourse.st")
    private Resource courseSystemPrompt;

    public String chat(String convId, String prompt) {

        String response = chatClient.prompt().user(prompt)
                // Conversation id must be set to something.
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, convId))
                .call()
                .content();

        return response;
    }

    public String queryVectorStore(String convId, String prompt) {

        SearchRequest searchRequest = SearchRequest.builder()
                .query(prompt)
                .topK(3)
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
