package com.example.mcp.mcp;

import com.example.mcp.service.ForwardService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(scanBasePackages = "com.example.mcp")
@ConfigurationPropertiesScan
public class McpServerApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(McpServerApplication.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        ConfigurableApplicationContext ctx = app.run(args);

        ForwardService forwardService = ctx.getBean(ForwardService.class);
        StdioJsonRpcServer server = new StdioJsonRpcServer(forwardService);
        server.run();
    }
}
