package com.example.mcp.client;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder,
            ToolCallbackProvider toolCallbackProvider) {
        System.out.println("toolCallbackProvider: " + toolCallbackProvider);
        return chatClientBuilder.defaultToolCallbacks(toolCallbackProvider).build();
    }
}
