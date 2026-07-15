package com.computrade.course.spring.ai.memory.controller;

import com.computrade.course.spring.ai.memory.service.MemoryService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/memory" )
@RequiredArgsConstructor
public class MemoryController {

    private final MemoryService memoryService;

    @GetMapping("/chat")
    public ResponseEntity<String> chat(String prompt) {
        String chatResponse = memoryService.chat(prompt);
        return ResponseEntity.ok(chatResponse);
    }


    @GetMapping("/chat/session")
    public ResponseEntity<String> chat(@RequestParam String prompt, HttpSession session) {

        String sessionId = session.getId();

        // Keep Data for user Session, so that the conversation is remembered for the same user session
        String chatResponse = memoryService.chat(sessionId, prompt);
        return ResponseEntity.ok(chatResponse);
    }

    @GetMapping("/chat/{userId}")
    public ResponseEntity<String> chat(
            @PathVariable String userId,
            @RequestParam String prompt) {

        // Keep data for userId, so that the conversation is remembered for the same userId
        String chatResponse = memoryService.chat(userId, prompt);
        return ResponseEntity.ok(chatResponse);
    }


}
