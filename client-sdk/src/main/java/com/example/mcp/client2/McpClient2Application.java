package com.example.mcp.client2;

import com.example.mcp.client2.config.McpSseClientProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({McpSseClientProperties.class})
public class McpClient2Application {

    public static void main(String[] args) {
        SpringApplication.run(McpClient2Application.class, args);
    }
}
