package com.computrade.course.spring.ai.mcp.client.service;

import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;


@RequiredArgsConstructor
@Service
public class ChatService {
    private static final Logger log = LoggerFactory.getLogger(ChatService.class);
    private final List<McpAsyncClient> asyncClients;
    private final ChatClient chatClient;

    public Flux<String> chatStream(String prompt) {
        return Flux.defer(() -> {
            // Log added before calling chatClient
            log.info("1. [START] ChatClient streaming call on thread [{}] for prompt: {}",
                    Thread.currentThread().getName(), prompt);

            return chatClient.prompt()
                    .user(prompt)
                    .stream()
                    .content()
                    .doOnNext(token -> log.info("2. [RESPONSE READY] Chunk received on [{}]: {}", Thread.currentThread().getName(), token));
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<String> chatWithMcpServer(String prompt) {

        // Defer execution to Mono so it runs asynchronously
        // boundedElastic offloads the blocking chatClient.call() operation off the main Event Loop thread and onto a dedicated background thread pool
        return Mono.fromCallable(() -> {
                    log.info("1. [START] ChatClient streaming call on thread [{}] for prompt: {}", Thread.currentThread().getName(), prompt);      // Simulating or running the LLM/MCP Tool call
                    String result = chatClient.prompt().user(prompt).call().content();
                    return result;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(res -> log.info("2. [RESPONSE READY] Returned to subscriber on thread: {}", Thread.currentThread().getName()));
    }


    public Mono<List<McpSchema.Tool>> getAsyncTools() {

        if (asyncClients.isEmpty()) {
            return Mono.error(new IllegalStateException("No Async MCP Clients registered!"));
        }

        McpAsyncClient mcpAsyncClient = asyncClients.get(0);
        return mcpAsyncClient.listTools()
                .map(McpSchema.ListToolsResult::tools);
    }

}
