package com.example.mcp.client2.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@ConfigurationProperties(prefix = "mcp.client")
public class McpSseClientProperties {

    private Map<String, SseParameters> connections;

    @Data
    public static class SseParameters {
        private String url;
        private String sseEndpoint = "/sse";
        private int timeoutSeconds = 50;
        private String clientName;
        private String clientVersion = "1.0.0";
    }
}
