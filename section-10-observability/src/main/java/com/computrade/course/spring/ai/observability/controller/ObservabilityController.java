package com.computrade.course.spring.ai.observability.controller;

import com.computrade.course.spring.ai.observability.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/observability")
@RequiredArgsConstructor
public class ObservabilityController {

    private final ChatService chatService;

    @GetMapping("/chat/{userId}")
    public ResponseEntity<String> chat(
            @PathVariable String userId,
            @RequestParam String prompt) {

        // Keep data for userId, so that the conversation is remembered for the same userId
        String chatResponse = chatService.chat(userId, prompt);
        return ResponseEntity.ok(chatResponse);
    }

    @GetMapping("/chat/{userId}/vectorStore")
    public ResponseEntity<String> queryVectorStore(@PathVariable String userId,  @RequestParam String prompt) {
        String response = chatService.queryVectorStore(userId, prompt);
        return ResponseEntity.ok(response);
    }

}
