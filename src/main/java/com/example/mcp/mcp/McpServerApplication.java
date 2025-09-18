package com.example.mcp.mcp;

import com.example.mcp.service.ForwardService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
// import removed
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = "com.example.mcp")
@ConfigurationPropertiesScan
public class McpServerApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(McpServerApplication.class);
        app.setWebApplicationType(WebApplicationType.REACTIVE);
        app.run(args);
    }

    @Bean
    public ToolCallbackProvider forwardTools(ForwardService forwardService) {
        return MethodToolCallbackProvider.builder().toolObjects(new ForwardTool(forwardService)).build();
    }
}
