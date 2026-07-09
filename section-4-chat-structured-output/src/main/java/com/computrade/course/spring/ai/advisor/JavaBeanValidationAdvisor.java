package com.computrade.course.spring.ai.advisor;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.util.JacksonUtils;
import tools.jackson.databind.json.JsonMapper;

import java.util.Set;
import java.util.stream.Collectors;

public class JavaBeanValidationAdvisor implements CallAdvisor {
    private static final Log logger = LogFactory.getLog(JavaBeanValidationAdvisor.class);

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final JsonMapper jsonMapper = JacksonUtils.getDefaultJsonMapper(); // in Jackson 3, ObjectMapper is replaced with JsonMapper for better performance and features
    private final Class<?> targetClass;
    private final int maxRepeatAttempts;
    private final int order;

    public JavaBeanValidationAdvisor(Class<?> targetClass, int maxRepeatAttempts, int order) {
        this.targetClass = targetClass;
        this.maxRepeatAttempts = maxRepeatAttempts;
        this.order = order;
    }

    @Override
    public @NonNull ChatClientResponse adviseCall(@NonNull ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        ChatClientResponse chatClientResponse;;
        int repeatCounter = 0;
        boolean isValidationSuccess;
        ChatClientRequest processedChatClientRequest = chatClientRequest;

        do {
            repeatCounter++;
            chatClientResponse = callAdvisorChain.copy(this).nextCall(processedChatClientRequest);

            try {
                String json = chatClientResponse.chatResponse().getResult().getOutput().getText();
                Object obj = jsonMapper.readValue(json, targetClass);

                // Validate Annotation Validator (@Size, @NotBlank)
                Set<ConstraintViolation<Object>> violations = validator.validate(obj);
                if (!violations.isEmpty()) {
                    isValidationSuccess = false;
                    if (logger.isWarnEnabled()) {
                        logger.warn("JSON validation failed: " + violations);
                    }
                    // Activate the self healing mechanism by augmenting the prompt with validation errors
                    processedChatClientRequest = activateSelfHealing(chatClientRequest, violations);
                }else{
                    isValidationSuccess = true;
                }
            } catch (Exception e) { // In Case Json is Broken
                isValidationSuccess = false;
                processedChatClientRequest = chatClientRequest.mutate().prompt(
                        chatClientRequest.prompt().augmentUserMessage(user -> user.mutate()
                                .text(user.getText() + "\nInvalid JSON format: " + e.getMessage()).build())
                ).build();
            }
        } while (!isValidationSuccess && repeatCounter <= maxRepeatAttempts);

        return chatClientResponse;
    }

    private static @NonNull ChatClientRequest activateSelfHealing(ChatClientRequest chatClientRequest, Set<ConstraintViolation<Object>> violations) {
        ChatClientRequest processedChatClientRequest;
        String errorMessages = violations.stream()
                .map(v -> v.getPropertyPath() + " " + v.getMessage())
                .collect(Collectors.joining("; "));

        String fixPrompt = System.lineSeparator() +
                "CRITICAL: Your output failed Java validation constraints: " + errorMessages +
                ". Please re-generate the JSON and strictly satisfy these constraints.";

        Prompt augmentedPrompt = chatClientRequest.prompt().augmentUserMessage(user -> user.mutate()
                .text(user.getText() + fixPrompt).build());

        processedChatClientRequest = chatClientRequest.mutate().prompt(augmentedPrompt).build();
        return processedChatClientRequest;
    }

    @Override
    public @NonNull String getName() {
        return "Custom Java Bean Validation Advisor";
    }
    @Override
    public int getOrder() {
        return this.order;
    }
}
