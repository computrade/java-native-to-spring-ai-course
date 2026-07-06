package com.computrade.course.spring.ai.controller;

import com.computrade.course.spring.ai.service.GeminiChatAdvisorsSafeGuardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/gemini")
@RequiredArgsConstructor
public class GeminiChatGuardAdvisorController {

    private final GeminiChatAdvisorsSafeGuardService geminiChatAdvisorsSafeGuardService;

    @GetMapping("/chat/guard")
    public ResponseEntity<String> chat(@Valid String userQuestion) {
        try {
            String chatResponse = geminiChatAdvisorsSafeGuardService.getChatResponseWithSafeGuardAdvisors(userQuestion);
            return ResponseEntity.ok(chatResponse);
        } catch (Exception e) {
            log.error("Error processing chat: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error processing chat: " + e.getMessage());
        }
    }

    @GetMapping("/chat/guard/custom")
    public ResponseEntity<String> chatGuardCustom(@Valid String userQuestion) {
        try {
            String chatResponse = geminiChatAdvisorsSafeGuardService.getChatResponseWithCustomSafeGuardAdvisors(userQuestion);
            return ResponseEntity.ok(chatResponse);
        } catch (Exception e) {
            log.error("Error processing chat: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error processing chat: " + e.getMessage());
        }
    }


}
