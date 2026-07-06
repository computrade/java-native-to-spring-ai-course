package com.computrade.course.spring.ai.advisor;

import org.jspecify.annotations.NonNull;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.core.Ordered;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CaseInsensitiveSafeGuardAdvisor implements CallAdvisor {

    private static final String DEFAULT_FAILURE_RESPONSE = "CaseInsensitiveSafeGuardAdvisor: I'm unable to respond to that due to sensitive content. Please rephrase your question.";

    private final List<String> lowercaseSensitiveWords;

    public CaseInsensitiveSafeGuardAdvisor(List<String> sensitiveWords) {
        // Pre-convert to lowercase at startup to optimize execution performance
        if (!CollectionUtils.isEmpty(sensitiveWords)) {
            this.lowercaseSensitiveWords = sensitiveWords.stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());
        } else {
            this.lowercaseSensitiveWords = List.of();
        }
    }

    @Override
    public @NonNull ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, @NonNull CallAdvisorChain callAdvisorChain) {

        String promptContent = chatClientRequest.prompt().getContents();
        // 2. Normalize user input to lowercase
        String lowerCaseContent = promptContent.toLowerCase();

        // 3. Perform case-insensitive matching against our pre-processed list
        if (!this.lowercaseSensitiveWords.isEmpty()
                && this.lowercaseSensitiveWords.stream().anyMatch(lowerCaseContent::contains)) {

            // Short-circuit the pipeline and return the fallback message
            return createFailureResponse(chatClientRequest);
        }

        // 4. Content is clean, move to the next advisor or call the LLM
        return callAdvisorChain.nextCall(chatClientRequest);
    }

    private ChatClientResponse createFailureResponse(ChatClientRequest chatClientRequest) {
        return ChatClientResponse.builder()
                .chatResponse(ChatResponse.builder()
                        .generations(List.of(new Generation(new AssistantMessage(DEFAULT_FAILURE_RESPONSE))))
                        .build())
                .context(Map.copyOf(chatClientRequest.context()))
                .build();
    }

    @Override
    public String getName() {
        return "CaseInsensitiveSafeGuardAdvisor";
    }

    @Override
    public int getOrder() {
        // Always run at the absolute front of the line to catch unsafe content early
        return Ordered.HIGHEST_PRECEDENCE;
    }
}

