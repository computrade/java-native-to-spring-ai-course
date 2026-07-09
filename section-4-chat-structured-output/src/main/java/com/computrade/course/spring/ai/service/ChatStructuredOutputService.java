package com.computrade.course.spring.ai.service;

import com.computrade.course.spring.ai.advisor.JavaBeanValidationAdvisor;
import com.computrade.course.spring.ai.model.CarDetails;
import com.computrade.course.spring.ai.model.ProductInfo;
import com.computrade.course.spring.ai.model.StoreCatalog;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.StructuredOutputValidationAdvisor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatStructuredOutputService {

    private final ChatClient defaultChatClient;
    private final LocalValidatorFactoryBean validatorFactory;
    private final JsonMapper jsonMapper;


    public List<String> getTopSkills(String topic) {

        return defaultChatClient.prompt()
                .user(u -> u.text("Give me a list of the top 5 most popular tools or skills for: {topic}")
                        .param("topic", topic))
                .call()
                .entity(new ParameterizedTypeReference<List<String>>() {
                });


    }

    public Map<String, String> getConceptGlossary(String concept) {

        return defaultChatClient.prompt()
                .user(u -> u.text("Provide exactly 3 core sub-concepts related to {concept}. " +
                                "Return them where the sub-concept is the key and its short description is the value.")
                        .param("concept", concept))
                .call()
                .entity(new ParameterizedTypeReference<Map<String, String>>() {});
    }

    public CarDetails getCarSpecification(String modelName) {

        return defaultChatClient.prompt()
                .user(u -> u.text("Provide detailed professional technical specifications for the vehicle: {modelName}")
                        .param("modelName", modelName))
                .call()
                .entity(CarDetails.class); // Magically deserialize the structured output into a CarDetails object
    }

    public List<ProductInfo> getProductCatalog(String category) {

        return defaultChatClient.prompt()
                .user(u -> u.text("Generate a list of 3 popular and real products in the category: {category}")
                        .param("category", category))
                .call()
                .entity(new ParameterizedTypeReference<List<ProductInfo>>() {});
    }

    public StoreCatalog getStoreCatalog(String category) {

        StructuredOutputValidationAdvisor validationAdvisor = StructuredOutputValidationAdvisor.builder()
                .outputType(StoreCatalog.class) // התשתית תריץ אוטומטית את JsonSchemaGenerator.generateForType()
                .maxRepeatAttempts(3)          // מספר הניסיונות החוזרים לתיקון מול ה-LLM במקרה של כישלון
                .build();

        return defaultChatClient.prompt()
                .user(u -> u.text("Generate a realistic electronics store recommendations catalog for the category: {category}. " +
                                "Provide a fitting store name and exactly 5 top featured products.")
                        .param("category", category))
                .advisors(validationAdvisor)
                .call()
                .entity(StoreCatalog.class);
    }

    public StoreCatalog getStoreCatalogWithCustomValidation(String category) {

        JavaBeanValidationAdvisor customAdvisor = new JavaBeanValidationAdvisor(
                StoreCatalog.class,// Output Type for validation
                3,  // Max Fix Attempts in case of validation failure
                20   // Advisor Order (lower number means higher priority)
        );

        return defaultChatClient.prompt()
                .user(u -> u.text("Generate a realistic electronics store recommendations catalog for the category: {category}. " +
                                "Provide a fitting store name and exactly 5 top featured products.")
                        .param("category", category))
                .advisors(customAdvisor)
                .call()
                .entity(StoreCatalog.class);
    }
}
