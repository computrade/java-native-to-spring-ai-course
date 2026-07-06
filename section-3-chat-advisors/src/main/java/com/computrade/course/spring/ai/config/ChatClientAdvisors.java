package com.computrade.course.spring.ai.config;


import com.computrade.course.spring.ai.advisor.CaseInsensitiveSafeGuardAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ChatClientAdvisors {

    @Bean
    public ChatClient defaultChatClient(ChatClient.Builder builder) {
        return builder
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }


    @Bean
    public ChatClient chatClientWithLoggerBuilder(ChatClient.Builder builder) {

        return builder
                .defaultAdvisors(getFinishReasonLoggerAdvisorWithBuilder())
                .build();
    }

    @Bean
    public ChatClient chatClientWithLoggerConstructor(ChatClient.Builder builder) {

        return builder
                .defaultAdvisors(getFinishReasonLoggerAdvisorWithConst())
                .build();
    }

    @Bean
    public ChatClient chatClientWithSafeGuardAdvisors(ChatClient.Builder builder) {

        return builder
                .defaultAdvisors(new SimpleLoggerAdvisor(),getSafeGuardAdvisor())
                .build();
    }


    @Bean
    public ChatClient chatClientWithCustomSafeGuardAdvisors(ChatClient.Builder builder) {

        return builder
                .defaultAdvisors(new SimpleLoggerAdvisor(),getCustomSafeGuardAdvisor())
                .build();
    }




    private SimpleLoggerAdvisor getFinishReasonLoggerAdvisorWithBuilder() {
        SimpleLoggerAdvisor finishReasonLogger = SimpleLoggerAdvisor.builder()
                // Suppress or format the request log if desired (or leave out to use default request format)
                .requestToString(request -> "LoggerAdvisorWithBuilder: Sending Prompt...")
                // Isolate exactly what you want from the response
                .responseToString(chatResponse -> {
                    if (chatResponse != null && !chatResponse.getResults().isEmpty()) {
                        var finishReason = chatResponse.getResult().getMetadata().getFinishReason();
                        return "AI Finish Reason: " + finishReason;
                    }
                    return "AI Finish Reason: UNKNOWN";
                }).order(10)  // Execution order priority
                .build();

        return finishReasonLogger;
    }

    private SimpleLoggerAdvisor getFinishReasonLoggerAdvisorWithConst() {
        SimpleLoggerAdvisor finishReasonLogger = new SimpleLoggerAdvisor(
                request -> "LoggerAdvisorWithConst: Processing prompt...", // Request function
                chatResponse -> {                  // Response function
                    if (chatResponse != null && chatResponse.getResult() != null) {
                        return "AI Finish Reason: " + chatResponse.getResult().getMetadata().getFinishReason() + " Usage: " + chatResponse.getMetadata().getUsage();
                    }
                    return "No metadata found";
                },
                10 // Execution order priority
        );

        return finishReasonLogger;
    }

    private SafeGuardAdvisor getSafeGuardAdvisor() {

        SafeGuardAdvisor safeGuardAdvisor = SafeGuardAdvisor.builder()
                .sensitiveWords(List.of("Password", "SSN","credit card","secret key")).order(0).build();
        return safeGuardAdvisor;
    }

    private CaseInsensitiveSafeGuardAdvisor getCustomSafeGuardAdvisor() {
        CaseInsensitiveSafeGuardAdvisor customSafeGuardAdvisor = new CaseInsensitiveSafeGuardAdvisor(List.of("Password", "SSN", "credit card", "secret key"));
        return customSafeGuardAdvisor;
    }

}
