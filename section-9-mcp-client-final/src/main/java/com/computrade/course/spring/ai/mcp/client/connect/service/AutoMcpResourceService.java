package com.computrade.course.spring.ai.mcp.client.connect.service;

import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AutoMcpResourceService {

    private final McpSyncClient mcpClient;

    public AutoMcpResourceService(List<McpSyncClient> mcpSyncClients) {
        if (mcpSyncClients == null || mcpSyncClients.isEmpty()) {
            throw new IllegalStateException("No McpSyncClient beans found in Spring Context!");
        }
        // Just for simplicity - Grab the first MCP client connection
        mcpClient = mcpSyncClients.getFirst();

    }


    /**
     * Automatically discovers registered MCP resources
     */
    public String getAvailableResourcesContext() {

        McpSchema.ListResourcesResult resourcesList = mcpClient.listResources();

        // 2. Aggregate resource metadata & URIs into a readable text format
        String availableResourcesContext = resourcesList.resources().stream()
                .map(res -> String.format("- URI: %s | Name: %s | Description: %s",
                        res.uri(), res.name(), res.description()))
                .collect(Collectors.joining("\n"));

        return availableResourcesContext;
    }
}
