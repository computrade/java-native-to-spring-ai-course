package com.computrade.course.spring.ai.tools.controller;

import com.computrade.course.spring.ai.tools.model.StockQuote;
import com.computrade.course.spring.ai.tools.service.ChatService;
import com.computrade.course.spring.ai.tools.service.StockMarketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/tools" )
@RequiredArgsConstructor
public class ToolsController {

    private final StockMarketService stockMarketService;
    private final ChatService chatService;

    @GetMapping("/quote")
    public ResponseEntity<StockQuote> getMarketQuote(String symbol) {
        return ResponseEntity.ok(stockMarketService.fetchStockPrice(symbol));
    }


    @GetMapping("/chat/tool")
    public ResponseEntity<String> chatWithQuoteTool(String prompt) {
        return ResponseEntity.ok(chatService.chatWithQuoteTool(prompt));

    }

}
