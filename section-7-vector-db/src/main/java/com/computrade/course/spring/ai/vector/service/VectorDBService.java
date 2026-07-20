package com.computrade.course.spring.ai.vector.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VectorDBService {

    private final ChatClient chatClient;
    private final EmbeddingModel embeddingModel;

    public Map<String,EmbeddingResponse> embed(String prompt) {



        EmbeddingResponse embeddingResponse = this.embeddingModel.embedForResponse(List.of(prompt));
        return Map.of("embedding", embeddingResponse);
    }


    public String chat(String prompt) {

            String response = chatClient.prompt().user(prompt)
                    // Conversation id must be set to something.
                    .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID,"default"))
                    .call()
                    .content();

            return response;
    }

    public String chat(String convId, String prompt) {

        String response = chatClient.prompt().user(prompt)
                // Conversation id must be set to something.
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, convId))
                .call()
                .content();

        return response;
    }
}
