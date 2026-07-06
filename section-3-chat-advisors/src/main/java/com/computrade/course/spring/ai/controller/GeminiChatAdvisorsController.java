package com.computrade.course.spring.ai.controller;

import com.computrade.course.spring.ai.model.AdviceRequest;
import com.computrade.course.spring.ai.service.GeminiChatAdvisorsService;
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
public class GeminiChatAdvisorsController {

    private final GeminiChatAdvisorsService geminiChatService;

    @GetMapping("/chat/log/default")
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

    @GetMapping("/chat/log/builder")
    public ResponseEntity<String> chatWithLoggerBuilder(@Valid @ParameterObject AdviceRequest request) {
        try {
            String chatResponse = geminiChatService.getChatResponseWithLoggerBuilder(
                    String.valueOf(request.age()),
                    request.risk().getLabel(),
                    request.prompt());
            return ResponseEntity.ok(chatResponse);
        } catch (Exception e) {
            log.error("Error processing chat: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error processing chat: " + e.getMessage());
        }
    }

    @GetMapping("/chat/log/constructor")
    public ResponseEntity<String> chatWithLoggerConstructor(@Valid @ParameterObject AdviceRequest request) {
        try {
            String chatResponse = geminiChatService.getChatResponseWithLoggerConstructor(
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
