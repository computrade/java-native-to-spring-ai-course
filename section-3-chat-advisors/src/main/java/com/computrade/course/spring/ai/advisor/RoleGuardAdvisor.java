package com.computrade.course.spring.ai.advisor;

import com.computrade.course.spring.ai.model.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class RoleGuardAdvisor implements CallAdvisor {

    private static final String DEFAULT_FAILURE_RESPONSE = "🛑 LLM access denied for your ROLE.";

    private final Set<UserRole> allowedRoles = Set.of(UserRole.ANALYST, UserRole.USER, UserRole.ADMIN);

    @Override
    public @NonNull ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, @NonNull CallAdvisorChain callAdvisorChain) {

        UserRole userRole = (UserRole) chatClientRequest.context().get("ROLE");
        if (userRole == null || !allowedRoles.contains(userRole)) {
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
    public @NonNull String getName() {
        return RoleGuardAdvisor.class.getName();
    }

    @Override
    public int getOrder() {
        // Always run at the absolute front of the line to catch unsafe content early
        return Ordered.HIGHEST_PRECEDENCE+1;
    }

}

