package com.computrade.course.spring.ai.tools.controller;

import com.computrade.course.spring.ai.tools.model.StockQuote;
import com.computrade.course.spring.ai.tools.model.Tenant;
import com.computrade.course.spring.ai.tools.service.ChatService;
import com.computrade.course.spring.ai.tools.service.FinnHubStockMarketService;
import com.computrade.course.spring.ai.tools.service.StockMarketSecureToolService;
import com.computrade.course.spring.ai.tools.service.StockMarketToolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/tools")
@RequiredArgsConstructor
public class ToolsController {

    private final FinnHubStockMarketService finnHubStockMarketService;
    private final ChatService chatService;
    private final ObjectMapper objectMapper;
    private final StockMarketToolService stockMarketToolService;

    @GetMapping("/quote")
    public ResponseEntity<StockQuote> getMarketQuote(String symbol) {
        return ResponseEntity.ok(finnHubStockMarketService.getSymbolQuote(symbol));
    }


    @GetMapping("/chat/tool/quote")
    public ResponseEntity<String> chatWithQuoteTool(String prompt) {
        return ResponseEntity.ok(chatService.chatWithQuoteTool(prompt));

    }


    @GetMapping("/schema")
    public ResponseEntity<List<Map<String, Object>>> getToolsSchema() {
        // 1. Create a provider from our annotated Java bean
        MethodToolCallbackProvider provider = MethodToolCallbackProvider.builder().toolObjects(stockMarketToolService).build();

        // 2. Extract all tool callbacks generated via Reflection
        List<ToolCallback> callbacks = Arrays.asList(provider.getToolCallbacks());

        // 3. Map each tool to its underlying JSON Schema structure
        List<Map<String, Object>> schemas = callbacks.stream()
                .map(callback -> {
                    JsonNode parsedSchema = objectMapper.readTree(callback.getToolDefinition().inputSchema());
                    return Map.of(
                            "toolName", callback.getToolDefinition().name(),
                            "description", callback.getToolDefinition().description(),
                            "jsonSchema", parsedSchema); // <-- This is the exact JSON Schema sent to the LLM
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(schemas);
    }

    @GetMapping("/chat/tool/context/quote")
    public ResponseEntity<String> chatWithQuoteToolContext(String prompt, Tenant tenant) {
        return ResponseEntity.ok(chatService.chatWithQuoteToolCotext(prompt, tenant));

    }


    //  Returns a beautifully formatted JSON object on screen
    @GetMapping(value = "/chat/news", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> chatWithNewsTool(String prompt) {
    // 1. Get the raw string response containing the direct tool output JSON
        String rawChatResponse = chatService.chatWithQuoteTool(prompt);

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
