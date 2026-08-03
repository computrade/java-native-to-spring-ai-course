package com.computrade.course.spring.ai.mcp.client.controller;


import com.computrade.course.spring.ai.mcp.client.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@RestController
@RequestMapping("/api/mcp")
@RequiredArgsConstructor
public class McpClientController {


    private final ChatService chatService;
    private final ObjectMapper objectMapper;




    @GetMapping("/chat")
    public ResponseEntity<String> chat(String prompt) {
        return ResponseEntity.ok(chatService.chatWithMcpServer(prompt));

    }


    //  Returns a beautifully formatted JSON object on screen
    @GetMapping(value = "/chat/news", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> chatWithNewsTool(String prompt) {
        // 1. Get the raw string response containing the direct tool output JSON
        String rawChatResponse = chatService.chatWithMcpServer(prompt);
        try {
            // 2. Try to parse the string back into a structural JSON object (List/Map)
            // so Spring's MessageConverter will pretty-print it out to the browser natively
            Object jsonObject = objectMapper.readValue(rawChatResponse, Object.class);
            return ResponseEntity.ok(jsonObject);
        } catch (Exception e) {
            // Fallback in case the user prompt triggered a normal LLM text response instead of the tool
            log.warn("Response was not a valid JSON structure, returning as plain string");
            return ResponseEntity.ok(rawChatResponse);
        }

    }



}
