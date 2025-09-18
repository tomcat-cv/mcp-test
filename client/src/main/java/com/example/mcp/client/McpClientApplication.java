package com.example.mcp.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example.mcp")
public class McpClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(McpClientApplication.class, args);
    }
}


