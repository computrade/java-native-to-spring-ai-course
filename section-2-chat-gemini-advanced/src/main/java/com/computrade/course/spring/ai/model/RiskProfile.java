package com.computrade.course.spring.ai.model;

public enum RiskProfile {

    CONSERVATIVE("Conservative"),
    MODERATE("Moderate"),
    AGGRESSIVE("Aggressive");

    private final String label;

    // Constructor
    RiskProfile(String label) {
        this.label = label;
    }

    // Getter to retrieve the human-readable text
    public String getLabel() {
        return this.label;
    }
}
