package com.example.mcp.client2.service;

import java.util.Map;

import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MySyncMcpToolCallback {

    private final McpSyncClient mcpClient;
    private final McpSchema.Tool tool;

    public MySyncMcpToolCallback(McpSyncClient mcpClient, McpSchema.Tool tool) {
        this.mcpClient = mcpClient;
        this.tool = tool;
    }

    public McpSyncClient getMcpClient() {
        return mcpClient;
    }

    public McpSchema.Tool getTool() {
        return tool;
    }

    /**
     * 调用MCP工具
     */
    public Object callTool(Map<String, Object> arguments) {
        try {
            log.info("调用MCP工具: {} with arguments: {}", tool.name(), arguments);

            // 这里可以根据具体的工具类型进行调用
            // 实际的调用逻辑需要根据MCP协议的具体实现

            return "Tool executed successfully";
        } catch (Exception e) {
            log.error("调用MCP工具失败: {}", tool.name(), e);
            throw new RuntimeException("工具调用失败", e);
        }
    }

    /**
     * 获取工具名称
     */
    public String getToolName() {
        return tool.name();
    }

    /**
     * 获取工具描述
     */
    public String getToolDescription() {
        return tool.description() != null ? tool.description() : "";
    }
}
