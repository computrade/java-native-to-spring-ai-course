package com.computrade.course.spring.ai.controller;

import com.computrade.course.spring.ai.model.CarDetails;
import com.computrade.course.spring.ai.model.ProductInfo;
import com.computrade.course.spring.ai.model.StoreCatalog;
import com.computrade.course.spring.ai.service.ChatStructuredOutputService;
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
@RequestMapping("/api/gemini")
@RequiredArgsConstructor
public class GeminiChatStructuredOutputController {

    private final ChatStructuredOutputService chatStructuredOutputService;

    @GetMapping("/skills")
    public ResponseEntity<List<String>> getTopSkills(String topic) {
        List<String> topSkills = chatStructuredOutputService.getTopSkills(topic);
        return ResponseEntity.ok(topSkills);
    }

    @GetMapping("/glossary")
    public ResponseEntity<Map<String, String>> getConceptGlossary(String concept) {
        Map<String, String> glossary = chatStructuredOutputService.getConceptGlossary(concept);
        return ResponseEntity.ok(glossary);
    }


    @GetMapping("/car-details")
    public ResponseEntity<CarDetails> getCarSpecification(String modelName) {
        CarDetails carDetails = chatStructuredOutputService.getCarSpecification(modelName);
        return ResponseEntity.ok(carDetails);
    }

    @GetMapping("/catalog")
    public ResponseEntity<List<ProductInfo>> getProductCatalog(String category) {
        List<ProductInfo> ProductInfos = chatStructuredOutputService.getProductCatalog(category);
        return ResponseEntity.ok(ProductInfos);
    }

    @GetMapping("/store/catalog")
    public ResponseEntity<StoreCatalog> getStoreCatalog(String category) {
        StoreCatalog storeCatalog = chatStructuredOutputService.getStoreCatalog(category);
        return ResponseEntity.ok(storeCatalog);
    }
}
