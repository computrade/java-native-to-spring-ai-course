package com.computrade.course.spring.ai.service;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.*;
import org.springframework.ai.chat.messages.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor // Lombok automatically generates the constructor for the final field below
public class GeminiChatSystemPromptService {

    // Direct, immutable injection of our centralized bean
    private final ChatClient chatClient;

    // 1. Inject the markdown file from the resources folder
    @Value("classpath:prompts/system-prompt.md")
    private Resource systemPromptResource;

    /**
     * Executes a standard blocking call to Gemini
     */
    public String getChatResponse(String prompt) {
        return chatClient.prompt(prompt)
                .call()
                .content();
    }

    /**
     * Add System Prompt with new PromptTemplate()
     */
    public String getFinancialAdvice(String age, String risk, String userQuestion) {

        String systemTemplateText = getSystemPromptString();

        // 1. Instantiate the template using the PromptTemplateStringActions interface
        PromptTemplateStringActions stringActions = new PromptTemplate(systemTemplateText);

        // 2. Render the template into a plain Java String using a parameters map
        String renderedSystemPrompt = stringActions.render(Map.of(
                "userAge", age,
                "riskTolerance", risk
        ));

        // 3. Pass the fully rendered String directly into your ChatClient flow
        return this.chatClient.prompt()
                .system(renderedSystemPrompt) // Accepts the raw rendered text directly
                .user(userQuestion)
                .call()
                .content();
    }

    /**
     * Add System Prompt with PromptTemplateStringActions
     * Separation of Roles:
     * This approach allows you to group entire structural conversational structures
     * (System instructions, User input, or even preset Assistant few-shot examples)
     * into a unified object array (List<Message>) rather than merging single raw strings.
     */
    public String getFinancialAdviceWithChatActions(String age, String risk, String userQuestion) {

        String systemTemplateText = getSystemPromptString();
        String userTemplateText = "{userQuestion}";

        // 2. Combine the sub-templates into a collective ChatPromptTemplate object
        // ChatPromptTemplate explicitly implements the PromptTemplateChatActions interface
        PromptTemplateChatActions chatActions = new ChatPromptTemplate(List.of(
                new SystemPromptTemplate(systemTemplateText),
                new PromptTemplate(userTemplateText)
        ));

        // 2. Render the template into a plain Java String using a parameters map
        List<Message> resolvedMessages = chatActions.createMessages(Map.of(
                "userAge", age,
                "riskTolerance", risk,
                "userQuestion", userQuestion
        ));

        // 3. Pass the fully rendered String directly into your ChatClient flow
        return this.chatClient.prompt()
                .messages(resolvedMessages) // Accepts the raw rendered text directly
                .call()
                .content();
    }

    /**
     *  Rather than explicitly instantiating PromptTemplate objects manually,
     *  Spring AI 2.0.0 fully leans into the chatClient.prompt().system(s -> s.text(...).params(...))
     *  functional builder style.
     */
     public String getFinancialAdviceFunctional(
            String age,
            String risk,
            String userQuestion) {

        // Define the structured System Prompt template using single curly braces {parameter}
        String systemTemplateText = getSystemPromptString();

        return this.chatClient.prompt()
                // 1. Inject system text along with its corresponding parameters
                .system(sp -> sp
                        .text(systemTemplateText)
                        .params(Map.of(
                                "userAge", age,
                                "riskTolerance", risk
                        ))
                )
                // 2. Set the dynamic user question
                .user(userQuestion)
                // 3. Call the LLM and fetch the content string
                .call()
                .content();
    }


    private static @NonNull String getSystemPromptString() {
        String systemTemplateText = """
            You are a qualified AI Financial Advisor. 
            Tailor your response to the following client parameters:
            - Age: {userAge}
            - Risk Profile: {riskTolerance}
            
            Provide structured, compliant educational insights based on this context.
            """;
        return systemTemplateText;
    }


    public String getFinancialAdviceWithResource(String age, String risk, String userQuestion) {

        // 1. Instantiate the template using the PromptTemplateStringActions interface
        PromptTemplateStringActions stringActions = new PromptTemplate(systemPromptResource);

        // 2. Render the template into a plain Java String using a parameters map
        String renderedSystemPrompt = stringActions.render(Map.of(
                "userAge", age,
                "riskTolerance", risk
        ));

        // 3. Pass the fully rendered String directly into your ChatClient flow
        return this.chatClient.prompt()
                .system(renderedSystemPrompt) // Accepts the raw rendered text directly
                .user(userQuestion)
                .call()
                .content();
    }


}
