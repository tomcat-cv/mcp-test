package com.example.mcp.client;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = McpClientApplication.class)
class McpToolCallIT {
    @Autowired
    ChatClient chatClient;

    @Test
    void call() {
        String question = "请使用 forward_query_stream 工具查询 '测试查询' 并返回结果";
        System.out.println("问题: " + question);

        String reply = chatClient.prompt(question).call().content();

        System.out.println("回复: " + reply);
        assertThat(reply).isNotBlank();
    }

    @Test
    void startServer() {
    }
}
