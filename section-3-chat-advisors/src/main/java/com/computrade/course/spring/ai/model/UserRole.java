package com.computrade.course.spring.ai.model;

import lombok.Getter;

import java.util.EnumSet;
import java.util.Set;

@Getter
public enum UserRole {

    USER("USER"),
    ADMIN("ADMIN"),
    ANALYST("ANALYST"),
    GUEST("GUEST");

    // Getter to retrieve the human-readable text
    private final String label;

    // Constructor
    UserRole(String label) {
        this.label = label;
    }


}
