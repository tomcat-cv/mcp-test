package com.example.mcp.client2.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.mcp.client2.service.MySyncMcpToolCallback;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * MCP客户端测试配置类
 * 参考原始配置类实现
 */
@Slf4j
@Configuration
public class McpClientConfig {

    @Bean
    @ConditionalOnProperty(name = "mcp.client.enabled", havingValue = "true")
    public List<MySyncMcpToolCallback> functionCallbacks(List<McpSyncClient> mcpClients) {
        List<MySyncMcpToolCallback> result = new ArrayList<>();
        for (McpSyncClient mcpClient : mcpClients) {
            try {
                var tools = mcpClient.listTools(null);
                var callbacks = tools.tools()
                        .stream()
                        .map(tool -> new MySyncMcpToolCallback(mcpClient, tool))
                        .toList();
                result.addAll(callbacks);
                log.info("为MCP客户端注册了 {} 个工具回调", callbacks.size());
            } catch (Exception e) {
                log.error("获取MCP工具列表失败", e);
            }
        }
        return result;
    }

    @ConditionalOnProperty(name = "mcp.client.enabled", havingValue = "true")
    @Bean
    public List<McpSyncClient> mcpClient(McpSseClientProperties properties, ObjectMapper objectMapper) {
        List<McpSyncClient> ret = new ArrayList<>();
        
        if (properties.getConnections() == null || properties.getConnections().isEmpty()) {
            log.warn("没有配置MCP连接，跳过MCP客户端初始化");
            return ret;
        }

        for (final Map.Entry<String, McpSseClientProperties.SseParameters> entry : properties.getConnections()
                .entrySet()) {
            try {
                McpSseClientProperties.SseParameters value = entry.getValue();
                String connectionName = entry.getKey();

                log.info("正在初始化MCP客户端: {} -> {}", connectionName, value.getUrl());

                HttpClientSseClientTransport transport = HttpClientSseClientTransport.builder(value.getUrl())
                        .clientBuilder(HttpClient.newBuilder())
                        .sseEndpoint(value.getSseEndpoint())
                        .build();

                String clientName = value.getClientName() != null ? 
                    value.getClientName() : "spring-ai-mcp-client-" + connectionName;
                String clientVersion = value.getClientVersion();

                McpSchema.Implementation clientInfo = new McpSchema.Implementation(clientName, clientVersion);
                
                var mcpClient = McpClient.sync(transport)
                        .clientInfo(clientInfo)
                        .requestTimeout(Duration.ofSeconds(value.getTimeoutSeconds()))
                        .build();

                var init = mcpClient.initialize();
                log.info("MCP客户端 {} 初始化成功: {}", connectionName, init);
                
                ret.add(mcpClient);
                
            } catch (Exception e) {
                log.error("初始化MCP客户端失败: {}", entry.getKey(), e);
                // 继续初始化其他客户端，不中断整个流程
            }
        }
        
        log.info("成功初始化了 {} 个MCP客户端", ret.size());
        return ret;
    }
}
