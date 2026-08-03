package com.computrade.course.spring.ai.mcp.client.config;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Configuration
public class McpClientConfig {


    @Bean
    public ChatClient defaultChatClient(ChatClient.Builder builder,
                                        ToolCallbackProvider toolCallbackProvider) {
        ToolCallbackProvider cleanedProvider = wrapToolCallbackProvider(toolCallbackProvider);
        return builder
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .defaultTools(cleanedProvider)
                .build();
    }

    private ToolCallbackProvider wrapToolCallbackProvider(ToolCallbackProvider original) {
        return () -> {
            ToolCallback[] originalCallbacks = original.getToolCallbacks();
            if (originalCallbacks == null) {
                return new ToolCallback[0];
            }
            ToolCallback[] cleanedCallbacks = new ToolCallback[originalCallbacks.length];
            for (int i = 0; i < originalCallbacks.length; i++) {
                cleanedCallbacks[i] = new SchemaCleaningToolCallback(originalCallbacks[i]);
            }
            return cleanedCallbacks;
        };
    }

    private static class SchemaCleaningToolCallback implements ToolCallback {
        private final ToolCallback delegate;
        private final ToolDefinition cleanedDefinition;

        public SchemaCleaningToolCallback(ToolCallback delegate) {
            this.delegate = delegate;
            ToolDefinition originalDef = delegate.getToolDefinition();
            String cleanedSchema = cleanSchema(originalDef.inputSchema());
            this.cleanedDefinition = new SimpleToolDefinition(
                    originalDef.name(),
                    originalDef.description(),
                    cleanedSchema
            );
        }

        @Override
        public ToolDefinition getToolDefinition() {
            return this.cleanedDefinition;
        }

        @Override
        public String call(String toolInput) {
            return this.delegate.call(toolInput);
        }

        private String cleanSchema(String schema) {
            if (schema == null) {
                return null;
            }
            Pattern pattern = Pattern.compile("\"type\"\\s*:\\s*\\[\\s*([^\\]]+)\\s*\\]");
            Matcher matcher = pattern.matcher(schema);
            StringBuilder sb = new StringBuilder();
            while (matcher.find()) {
                String arrayContent = matcher.group(1);
                String targetType = "string";
                Pattern typePattern = Pattern.compile("\"([^\"]+)\"");
                Matcher typeMatcher = typePattern.matcher(arrayContent);
                while (typeMatcher.find()) {
                    String type = typeMatcher.group(1);
                    if (!"null".equalsIgnoreCase(type)) {
                        targetType = type;
                        break;
                    }
                }
                matcher.appendReplacement(sb, Matcher.quoteReplacement("\"type\": \"" + targetType + "\""));
            }
            matcher.appendTail(sb);
            return sb.toString();
        }
    }

    private record SimpleToolDefinition(String name, String description, String inputSchema) implements ToolDefinition {}

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public RestClient finnHubRestClient(RestClient.Builder builder) {
        return builder.baseUrl("https://finnhub.io/api/v1").build();
    }

}
