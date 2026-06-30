package com.computrade.course.spring.ai.controller;

import com.computrade.course.spring.ai.model.AdviceRequest;
import com.computrade.course.spring.ai.service.GeminiAdvancedChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/gemini")
@RequiredArgsConstructor
public class GeminiAdvancedChatController {

    private final GeminiAdvancedChatService geminiChatService;

    @GetMapping("/chat/options")
    public ResponseEntity<String> chat(@Valid @ParameterObject AdviceRequest request) {
        try {
            String chatResponse = geminiChatService.getChatResponse(
                    String.valueOf(request.age()),
                    request.risk().getLabel(),
                    request.prompt());
            return ResponseEntity.ok(chatResponse);
        } catch (Exception e) {
            log.error("Error processing chat: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error processing chat: " + e.getMessage());
        }
    }

    @GetMapping("/chat/local-options")
    public ResponseEntity<String> chatWithLocalOptions(@Valid @ParameterObject AdviceRequest request) {
        try {
            String chatResponse = geminiChatService.getChatResponseLocalOptions(
                    String.valueOf(request.age()),
                    request.risk().getLabel(),
                    request.prompt());
            return ResponseEntity.ok(chatResponse);
        } catch (Exception e) {
            log.error("Error processing chat: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error processing chat: " + e.getMessage());
        }
    }


}
