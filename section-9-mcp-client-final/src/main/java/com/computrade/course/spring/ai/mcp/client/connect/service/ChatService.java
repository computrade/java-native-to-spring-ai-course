package com.computrade.course.spring.ai.mcp.client.connect.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChatService {

    private final ChatClient chatClient;
    private final AutoMcpResourceService resourceService;

    public String chatWithMcpServer(String prompt) {
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    public String chatWithResources(String prompt) {

        String availableResourcesContext = resourceService.getAvailableResourcesContext();

        // 3. Send user query to ChatClient with automatically injected resource metadata
        return chatClient.prompt()
                .system(sys -> sys.text("""
                        You are an AI Assistant integrated with an MCP erver.
                        
                        The following MCP Resources are currently registered and available in the system:
                        {resources}
                        
                        CRITICAL FUNCTION CALLING RULES:
                        - To read any resource content, you MUST call the function named exactly 'read_mcp_resource' with the parameter 'uri'.
                        - DO NOT use trailing or leading underscores in function names. Use ONLY 'read_mcp_resource'.
                        
                        Use this resource catalog to understand what context and dynamic URIs exist.
                        Answer the user request accurately based on this system metadata.
                        """)
                        .param("resources", availableResourcesContext))
                .user(prompt)
                .call()
                .content();
    }

}
