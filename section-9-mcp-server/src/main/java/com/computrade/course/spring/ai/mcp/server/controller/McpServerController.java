package com.computrade.course.spring.ai.mcp.server.controller;


import com.computrade.course.spring.ai.mcp.server.model.StockQuote;
import com.computrade.course.spring.ai.mcp.server.service.FinnHubStockMarketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/tools")
@RequiredArgsConstructor
public class McpServerController {

    private final FinnHubStockMarketService finnHubStockMarketService;


    @GetMapping("/quote")
    public ResponseEntity<StockQuote> getMarketQuote(String symbol) {
        return ResponseEntity.ok(finnHubStockMarketService.getSymbolQuote(symbol));
    }




}
