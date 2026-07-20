package com.computrade.course.spring.ai.vector.util;

import org.jspecify.annotations.NonNull;
import org.springframework.ai.transformer.splitter.TextSplitter;

import java.util.ArrayList;
import java.util.List;

public class CustomRegexDocumentSplitter extends TextSplitter {

    private final String regex;

    public CustomRegexDocumentSplitter(String regex) {
        this.regex = regex;
    }

    @Override
    protected @NonNull List<String> splitText(String text) {
        List<String> chunks = new ArrayList<>();
        // פיצול הטקסט הגולמי לפי ה-Regex (האינדקסים של המודולים)
        String[] splitContent = text.split(regex);

        for (String chunk : splitContent) {
            if (!chunk.strip().isEmpty()) {
                chunks.add(chunk.strip());
            }
        }
        return chunks;
    }
}