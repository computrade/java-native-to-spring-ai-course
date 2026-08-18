package com.computrade.course.spring.ai.mcp.client.connect.service;

import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class McpResourceToolCallback {

    private final McpSyncClient mcpSyncClient;

    public McpResourceToolCallback(List<McpSyncClient> mcpSyncClients) {
        if (mcpSyncClients.isEmpty()) {
            throw new IllegalStateException("No McpSyncClient beans found");
        }
        this.mcpSyncClient = mcpSyncClients.getFirst();
    }

    /**
     * Tool callback exposed to the LLM allowing dynamic reading of MCP Resources.
     *
     * @param uri The URI of the MCP resource to read (e.g., stock://docs/architecture.md)
     * @return Raw text content of the requested resource
     */
    @Tool(name = "read_mcp_resource", description = "Reads and fetches content from a specific MCP Resource URI")
    public String readMcpResource(String uri) {
        var request = new McpSchema.ReadResourceRequest(uri);
        var result = mcpSyncClient.readResource(request);

        if (result != null && result.contents() != null && !result.contents().isEmpty()) {
            if (result.contents().get(0) instanceof McpSchema.TextResourceContents textContents) {
                return textContents.text();
            }
        }
        return "Resource at URI " + uri + " was empty or not found.";
    }
}
