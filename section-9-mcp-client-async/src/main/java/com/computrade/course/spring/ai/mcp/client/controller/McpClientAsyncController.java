package com.computrade.course.spring.ai.mcp.client.controller;


import com.computrade.course.spring.ai.mcp.client.service.ChatService;
import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mcp/async")
public class McpClientAsyncController {

    private final ChatService chatService;

    @GetMapping(value = "/chat")
    public Mono<String> chat(String prompt) {

        return chatService.chatWithMcpServer(prompt);

    }


    // Non-blocking call to fetch available time tools
    @GetMapping("/time-tools")
    public Mono<List<McpSchema.Tool>> getAsyncTools() {
        return chatService.getAsyncTools();
    }

}
