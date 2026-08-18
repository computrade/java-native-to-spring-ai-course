package com.computrade.course.spring.ai.mcp.client.async.controller;


import com.computrade.course.spring.ai.mcp.client.async.service.ChatService;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mcp")
public class McpClientAsyncController {

    private final ChatService chatService;

    @GetMapping(value = "/chat/stream")
    public Flux<String> chatStream(String prompt) {
        return chatService.chatStream(prompt);

    }


    @GetMapping(value = "/chat/async")
    public Mono<String> chat(String prompt) {
        return chatService.chatWithMcpServer(prompt);

    }


    // Non-blocking call to fetch available time tools
    @GetMapping("/async-tools")
    public Mono<List<McpSchema.Tool>> getAsyncTools() {
        return chatService.getAsyncTools();
    }

}
