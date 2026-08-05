package com.computrade.course.spring.ai.mcp.client.service;

import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatService {

    private final List<McpAsyncClient> asyncClients;
    private final ChatClient chatClient;

    public Mono<String> chatWithMcpServer(String prompt) {
        return Mono.fromCallable(() -> this.chatClient.prompt()
                        .user(prompt)
                        .call()
                        .content())
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<List<McpSchema.Tool>> getAsyncTools() {

        if (asyncClients.isEmpty()) {
            return Mono.error(new IllegalStateException("No Async MCP Clients registered!"));
        }

        McpAsyncClient timeClient = asyncClients.get(0);
        return timeClient.listTools()
                .map(McpSchema.ListToolsResult::tools);
    }

}
