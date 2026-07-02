package com.computrade.course.spring.ai.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdviceWithConfigRequest(

        @NotNull(message = "Age is required")
        @Min(value = 18, message = "Age must be at least 18")
        @Max(value = 120, message = "Age cannot exceed 120")
        Integer age,

        @NotNull(message = "Risk profile is required")
        RiskProfile risk,

        @NotNull(message = "Temperature is required")
        Double temperature,

        @Min(value = 1, message = "Max tokens must be a positive integer")
        Integer maxTokens,

        @NotBlank(message = "Prompt cannot be empty")
        String prompt

) {}
