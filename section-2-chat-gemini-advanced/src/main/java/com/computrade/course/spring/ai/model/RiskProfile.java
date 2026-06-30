package com.computrade.course.spring.ai.model;

import lombok.Getter;

@Getter
public enum RiskProfile {

    CONSERVATIVE("Conservative"),
    MODERATE("Moderate"),
    AGGRESSIVE("Aggressive");

    // Getter to retrieve the human-readable text
    private final String label;

    // Constructor
    RiskProfile(String label) {
        this.label = label;
    }

}
