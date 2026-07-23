package com.computrade.course.spring.ai.vector.controller;

import com.computrade.course.spring.ai.vector.service.PdfVectorDBService;
import com.computrade.course.spring.ai.vector.service.VectorDBRouterService;
import com.computrade.course.spring.ai.vector.service.VectorDBService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/vector-db" )
@RequiredArgsConstructor
public class VectorDBController {

    private final VectorDBService vectorDBService;
    private final VectorDBRouterService vectorDBRouterService;
    private final PdfVectorDBService pdfVectorDBService;
    
    @GetMapping("/embedding")
    public ResponseEntity<Map<String,EmbeddingResponse>> embed(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        Map<String,EmbeddingResponse> embeddingResponse = vectorDBService.embed(message);
        return ResponseEntity.ok(embeddingResponse);
    }

    @PostMapping("/ingest")
    public ResponseEntity<String> ingestData() {
        vectorDBService.ingestCoursesToVectorStore();
        return ResponseEntity.ok("Data sent to Vector Store successfully!");
    }

    @GetMapping("/chat/{userId}")
    public ResponseEntity<String> chat(
            @PathVariable String userId,
            @RequestParam String prompt) {

        // Keep data for userId, so that the conversation is remembered for the same userId
        String chatResponse = vectorDBService.chat(userId, prompt);
        return ResponseEntity.ok(chatResponse);
    }

    @GetMapping("/chat-rag/{userId}")
    public ResponseEntity<String> chatRag(  @PathVariable String userId, @RequestParam String prompt) {
        String chatResponse = vectorDBService.chatRag(userId, prompt);
        return ResponseEntity.ok(chatResponse);
    }






    @PostMapping("/ingest/pdf")
    public ResponseEntity<String> ingestPdf() {
        String response = vectorDBService.ingestLongPdf();
        return ResponseEntity.ok(response);
    }


    @GetMapping("/chat-rag/{userId}/route")
    public ResponseEntity<String> vectorDBRouterService(@PathVariable String userId,  @RequestParam String prompt) {
        String response = vectorDBRouterService.routeAndQuery(userId, prompt);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/ingest/pdfVectorStore")
    public ResponseEntity<String> ingestPdfVector() {
        String response = pdfVectorDBService.ingestLongPdf();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/chat-rag/{userId}/pdf")
    public ResponseEntity<String> chatPdfRag(@PathVariable String userId, @RequestParam String prompt) {
        String chatResponse = pdfVectorDBService.queryPdfVectorStore(userId, prompt);
        return ResponseEntity.ok(chatResponse);
    }

    @GetMapping("/chat-rag/{userId}/route/tables")
    public ResponseEntity<String> vectorDBRouterByTablesService(@PathVariable String userId,  @RequestParam String prompt) {
        String response = vectorDBRouterService.routeAndQueryByTable(userId, prompt);
        return ResponseEntity.ok(response);
    }

}
