package com.computrade.course.spring.ai.controller;

import com.computrade.course.spring.ai.service.GeminiChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gemini")
@RequiredArgsConstructor
public class GeminiChatController {

    private final GeminiChatService geminiChatService;

    @GetMapping("/chat")
    public String chat(@RequestParam String prompt) {
        return geminiChatService.getChatResponse(prompt);
    }

}
