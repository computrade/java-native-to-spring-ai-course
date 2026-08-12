package com.computrade.course.spring.ai.stream.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@Service
public class ChatService {
    private static final Logger log = LoggerFactory.getLogger(ChatService.class);
    private final ChatClient chatClient;

    public Flux<String> chat(String prompt) {

        log.info("1. START: calling chatClient on thread [{}]",Thread.currentThread().getName());
        return chatClient.prompt()
                .user(prompt)
                .stream()
                .content()
                .doOnNext(token -> log.info("Chunk received on [{}]: {}", Thread.currentThread().getName(), token));
    }

}
