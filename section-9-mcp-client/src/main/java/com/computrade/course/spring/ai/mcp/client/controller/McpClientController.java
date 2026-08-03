package com.computrade.course.spring.ai.mcp.client.controller;


import com.computrade.course.spring.ai.mcp.client.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/mcp")
@RequiredArgsConstructor
public class McpClientController {

    private final ChatService chatService;

    @GetMapping("/chat")
    public ResponseEntity<String> chat(String prompt) {
        return ResponseEntity.ok(chatService.chatWithMcpServer(prompt));

    }


}
