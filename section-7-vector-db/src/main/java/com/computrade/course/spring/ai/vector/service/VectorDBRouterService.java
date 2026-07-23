package com.computrade.course.spring.ai.vector.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class VectorDBRouterService {

    private final ChatClient chatClient;
    private final VectorDBService vectorDBService;

    public VectorDBRouterService(ChatClient.Builder chatClientBuilder, VectorDBService vectorDBService) {
        this.vectorDBService = vectorDBService;
        // We use local system prompt to defind the Router job.
        this.chatClient = chatClientBuilder
                .defaultSystem("""
                    You are a routing assistant. Analyze the user's question and classify it into exactly one of these categories:
                    - MY_COURSE: If the question is specifically about the Spring AI course syllabus, modules, or what is covered in this specific class.
                    - GENERAL: If the question is about courses in general, other topics, or general recommendations.
                    
                    Respond with ONLY the category name (MY_COURSE or GENERAL). No other text.
                    """)
                .build();
    }

    public String routeAndQuery(String covId, String userQuestion) {
        // LLM Decide what the user meant to ask
        String classification = this.chatClient.prompt()
                .user(userQuestion)
                .call()
                .content();

        log.info("User intent classified as: {}", classification);

        // Route the quest to the correct service with hybrid search
        if ("MY_COURSE".equals(classification)) {
            return vectorDBService.queryMyCourse(covId,userQuestion);
        } else {
            return vectorDBService.queryAllCourse(covId,userQuestion);
        }
    }
}
