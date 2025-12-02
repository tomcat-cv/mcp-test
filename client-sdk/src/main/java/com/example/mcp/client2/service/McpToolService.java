package com.example.mcp.client2.service;

import io.modelcontextprotocol.spec.McpSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * MCP工具服务
 * 用于管理和调用MCP工具
 */
@Slf4j
@Service
public class McpToolService {

    @Autowired(required = false)
    private List<MySyncMcpToolCallback> toolCallbacks;

    /**
     * 获取所有可用的工具
     */
    public List<MySyncMcpToolCallback> getAllTools() {
        if (toolCallbacks == null || toolCallbacks.isEmpty()) {
            log.warn("没有可用的MCP工具");
            return List.of();
        }
        return toolCallbacks;
    }

    /**
     * 根据名称获取工具
     */
    public Optional<MySyncMcpToolCallback> getToolByName(String toolName) {
        if (toolCallbacks == null || toolCallbacks.isEmpty()) {
            return Optional.empty();
        }

        return toolCallbacks.stream()
                .filter(callback -> callback.getToolName().equals(toolName))
                .findFirst();
    }

    /**
     * 调用指定的工具
     */
    public Object callTool(String toolName, Map<String, Object> arguments) {
        log.info("准备调用MCP工具: {} 参数: {}", toolName, arguments);

        Optional<MySyncMcpToolCallback> toolOpt = getToolByName(toolName);
        
        if (toolOpt.isEmpty()) {
            log.error("未找到工具: {}", toolName);
            throw new IllegalArgumentException("工具不存在: " + toolName);
        }

        MySyncMcpToolCallback tool = toolOpt.get();
        
        try {
            // 调用MCP工具
            McpSchema.CallToolResult result = tool.getMcpClient().callTool(
                new McpSchema.CallToolRequest(toolName, arguments)
            );
            
            log.info("工具调用成功: {} 结果: {}", toolName, result);
            
            // 返回工具调用结果
            return result.content();
            
        } catch (Exception e) {
            log.error("调用MCP工具失败: {}", toolName, e);
            throw new RuntimeException("工具调用失败: " + e.getMessage(), e);
        }
    }

    /**
     * 检查工具是否可用
     */
    public boolean isToolAvailable(String toolName) {
        return getToolByName(toolName).isPresent();
    }

    /**
     * 获取工具数量
     */
    public int getToolCount() {
        return toolCallbacks != null ? toolCallbacks.size() : 0;
    }
}

