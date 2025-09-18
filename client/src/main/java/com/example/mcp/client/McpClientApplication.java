package com.example.mcp.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication(scanBasePackages = "com.example.mcp")
@Import(ClientConfig.class)
public class McpClientApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(McpClientApplication.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
    }
}
