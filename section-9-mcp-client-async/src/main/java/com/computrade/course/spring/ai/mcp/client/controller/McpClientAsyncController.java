package com.computrade.course.spring.ai.mcp.client.controller;


import com.computrade.course.spring.ai.mcp.client.service.ChatService;
import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/mcp/async")
public class McpClientAsyncController {

    private final List<McpAsyncClient> asyncClients;
    private final ChatService chatService;

    public McpClientAsyncController(List<McpAsyncClient> asyncClients,ChatService chatService) {
        this.asyncClients = asyncClients;
        this.chatService = chatService;
    }

    @GetMapping("/chat")
    public ResponseEntity<String> chat(String prompt) {
        return ResponseEntity.ok(chatService.chatWithMcpServer(prompt));

    }


    // Non-blocking call to fetch available time tools
    @GetMapping("/time-tools")
    public Mono<List<McpSchema.Tool>> getAsyncTools() {
        if (asyncClients.isEmpty()) {
            return Mono.error(new IllegalStateException("No Async MCP Clients registered!"));
        }

        McpAsyncClient timeClient = asyncClients.get(0);
        return timeClient.listTools()
                .map(McpSchema.ListToolsResult::tools);
    }

}
