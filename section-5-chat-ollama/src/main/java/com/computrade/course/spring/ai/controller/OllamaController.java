package com.computrade.course.spring.ai.controller;

import com.computrade.course.spring.ai.model.CarDetails;
import com.computrade.course.spring.ai.model.StoreCatalog;
import com.computrade.course.spring.ai.service.ChatOllamaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/ollama" )
@RequiredArgsConstructor
public class OllamaController {

    private final ChatOllamaService chatOllamaService;

    @GetMapping("/chat")
    public ResponseEntity<String> chat(String prompt) {
        String chatResponse = chatOllamaService.chat(prompt);
        return ResponseEntity.ok(chatResponse);
    }

    @GetMapping("/skills")
    public ResponseEntity<List<String>> getTopSkills(String topic) {
        List<String> topSkills = chatOllamaService.getTopSkills(topic);
        return ResponseEntity.ok(topSkills);
    }

    @GetMapping("/car/details")
    public ResponseEntity<CarDetails> getCarSpecification(String modelName) {
        CarDetails carDetails = chatOllamaService.getCarSpecification(modelName);
        return ResponseEntity.ok(carDetails);
    }

    @GetMapping("/car/catalog")
    public ResponseEntity<List<CarDetails>> getCarCatalog(String category) {
        List<CarDetails> carDetailsList = chatOllamaService.getCarCatalog(category);
        return ResponseEntity.ok(carDetailsList);
    }

    @GetMapping("/store/catalog/validation")
    public ResponseEntity<StoreCatalog> getStoreCatalog(String category) {
        StoreCatalog storeCatalog = chatOllamaService.getStoreCatalog(category);
        return ResponseEntity.ok(storeCatalog);
    }

}
