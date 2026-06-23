package com.computrade.course.spring.ai.controller;

import com.computrade.course.spring.ai.model.AdviceRequest;
import com.computrade.course.spring.ai.service.GeminiChatSystemPromptService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gemini")
@RequiredArgsConstructor
public class GeminiChatSystemPromptController {

    private final GeminiChatSystemPromptService geminiChatSystemPromptService;

    @GetMapping("/chat")
    public ResponseEntity<String> chat(@RequestParam String prompt) {
        String chatResponse = geminiChatSystemPromptService.getChatResponse(prompt);
        return ResponseEntity.ok(chatResponse);
    }


    @GetMapping("/advice")
    public ResponseEntity<String> getAdvice(@Valid @ParameterObject AdviceRequest request) {

        // Call the service that leverages PromptTemplateStringActions
        String adviceResponse = geminiChatSystemPromptService.getFinancialAdvice(
                String.valueOf(request.age()),
                request.risk().getLabel(),
                request.prompt());

        // Return a standard 200 OK response containing the AI text
        return ResponseEntity.ok(adviceResponse);
    }

    @GetMapping("/advice/actions")
    public ResponseEntity<String> getAdviceWithActions(@Valid @ParameterObject AdviceRequest request) {

        // Call the service that leverages PromptTemplateStringActions
        String adviceResponse = geminiChatSystemPromptService.getFinancialAdviceWithChatActions(
                String.valueOf(request.age()),
                request.risk().getLabel(),
                request.prompt());

        // Return a standard 200 OK response containing the AI text
        return ResponseEntity.ok(adviceResponse);
    }


    @GetMapping("/advice/functional")
    public ResponseEntity<String> getAdviceFunctional(@Valid @ParameterObject AdviceRequest request) {

        String adviceResponse = geminiChatSystemPromptService.getFinancialAdviceFunctional(
                String.valueOf(request.age()),
                request.risk().getLabel(),
                request.prompt());

        // Return a standard 200 OK response containing the AI text
        return ResponseEntity.ok(adviceResponse);
    }

    @GetMapping("/advice/resource")
    public ResponseEntity<String> getAdviceWithResource(@Valid @ParameterObject AdviceRequest request) {

        String adviceResponse = geminiChatSystemPromptService.getFinancialAdviceWithResource(
                String.valueOf(request.age()),
                request.risk().getLabel(),
                request.prompt());

        // Return a standard 200 OK response containing the AI text
        return ResponseEntity.ok(adviceResponse);
    }




}
