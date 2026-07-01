package com.computrade.course.spring.ai.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ConfigRequest(
        @NotNull(message = "Temperature is required")
        Double temperature,

        @Min(value = 1, message = "Max tokens must be a positive integer")
        Integer maxTokens,

        @NotBlank(message = "Prompt cannot be empty")
        String prompt

) {}
