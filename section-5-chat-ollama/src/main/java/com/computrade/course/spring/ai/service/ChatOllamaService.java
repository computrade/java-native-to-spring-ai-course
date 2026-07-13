package com.computrade.course.spring.ai.service;

import com.computrade.course.spring.ai.model.CarDetails;
import com.computrade.course.spring.ai.model.StoreCatalog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.StructuredOutputValidationAdvisor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatOllamaService {

    private final ChatClient defaultChatClient;
    
    public String chat(String prompt) {
        return defaultChatClient.prompt(prompt)
                .call()
                .content();
    }

    public List<String> getTopSkills(String topic) {

        return defaultChatClient.prompt()
                .user(u -> u.text("Give me a list of the top 5 most popular tools or skills for: {topic}")
                        .param("topic", topic))
                .call()
                .entity(new ParameterizedTypeReference<List<String>>() {
                });


    }

    public CarDetails getCarSpecification(String modelName) {

        return defaultChatClient.prompt()
                .user(u -> u.text("Provide detailed professional technical specifications for the vehicle: {modelName}")
                        .param("modelName", modelName))
                .call()
                .entity(CarDetails.class); // Magically deserialize the structured output into a CarDetails object
    }

    public List<CarDetails> getCarCatalog(String category) {

        return defaultChatClient.prompt()
                .user(u -> u.text("Generate a list of 3 popular and real vehicle in the category: {category}")
                        .param("category", category))
                .call()
                .entity(new ParameterizedTypeReference<List<CarDetails>>() {
                });
    }

    public StoreCatalog getStoreCatalog(String category) {

        StructuredOutputValidationAdvisor validationAdvisor = StructuredOutputValidationAdvisor.builder()
                .outputType(StoreCatalog.class) // Output Type for validation
                .maxRepeatAttempts(3)         // Max Fix Attempts in case of validation failure
                .build();

        return defaultChatClient.prompt()
                .user(u -> u.text("Generate a realistic electronics store recommendations catalog for the category: {category}. " +
                                "Provide a fitting store name and exactly 5 top featured products.")
                        .param("category", category))
                .advisors(validationAdvisor)
                .call()
                .entity(StoreCatalog.class);
    }


}
