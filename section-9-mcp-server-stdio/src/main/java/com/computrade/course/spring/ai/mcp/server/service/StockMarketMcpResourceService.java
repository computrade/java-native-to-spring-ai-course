package com.computrade.course.spring.ai.mcp.server.service;

import io.modelcontextprotocol.spec.McpSchema;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockMarketMcpResourceService {

    // 🟢 Inject the file directly from src/main/resources/docs/architecture.md
    @Value("classpath:docs/architecture.md")
    private Resource architectureDocResource;

    /**
     * Reads a Markdown documentation file directly from the classpath/resources directory.
     * URI: stock://docs/architecture.md
     */
    public McpSchema.ReadResourceResult getServerArchitectureDoc() {
        try {
            // Read file content from resources
            String markdownContent = StreamUtils.copyToString(
                    architectureDocResource.getInputStream(),
                    StandardCharsets.UTF_8
            );

            McpSchema.TextResourceContents textContents = McpSchema.TextResourceContents
                    .builder("stock://docs/architecture.md",markdownContent)
                    .build();

            return McpSchema.ReadResourceResult.builder(List.of(textContents)).build();

        } catch (IOException e) {
            throw new RuntimeException("Failed to read resource file from classpath: docs/architecture.md", e);
        }
    }
}
