package com.example.mcp.client2.controller;

import com.example.mcp.client2.model.McpQueryRequest;
import com.example.mcp.client2.model.McpQueryResponse;
import com.example.mcp.client2.model.ToolInfo;
import com.example.mcp.client2.service.McpToolService;
import com.example.mcp.client2.service.MySyncMcpToolCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * MCP工具Controller
 * 用于接收用户请求并调用下游MCP服务
 */
@Slf4j
@RestController
@RequestMapping("/api/mcp")
public class McpToolController {

    @Autowired
    private McpToolService mcpToolService;

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        int toolCount = mcpToolService.getToolCount();
        return ResponseEntity.ok("MCP Client SDK is running. Available tools: " + toolCount);
    }

    /**
     * 获取所有可用的工具列表
     */
    @GetMapping("/tools")
    public ResponseEntity<List<ToolInfo>> listTools() {
        try {
            List<MySyncMcpToolCallback> tools = mcpToolService.getAllTools();
            
            List<ToolInfo> toolInfos = tools.stream()
                    .map(tool -> new ToolInfo(
                            tool.getToolName(),
                            tool.getToolDescription(),
                            tool.getTool().inputSchema()
                    ))
                    .collect(Collectors.toList());
            
            log.info("返回 {} 个可用工具", toolInfos.size());
            return ResponseEntity.ok(toolInfos);
            
        } catch (Exception e) {
            log.error("获取工具列表失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 调用指定的MCP工具
     */
    @PostMapping("/call")
    public ResponseEntity<McpQueryResponse> callTool(@RequestBody McpQueryRequest request) {
        try {
            log.info("收到工具调用请求: toolName={}, arguments={}", 
                    request.getToolName(), request.getArguments());

            // 验证请求参数
            if (request.getToolName() == null || request.getToolName().trim().isEmpty()) {
                return ResponseEntity.ok(
                    new McpQueryResponse(false, "工具名称不能为空", null)
                );
            }

            // 检查工具是否存在
            if (!mcpToolService.isToolAvailable(request.getToolName())) {
                return ResponseEntity.ok(
                    new McpQueryResponse(false, "工具不存在: " + request.getToolName(), null)
                );
            }

            // 调用工具
            Object result = mcpToolService.callTool(
                request.getToolName(), 
                request.getArguments()
            );

            log.info("工具调用成功: {}", result);
            return ResponseEntity.ok(
                new McpQueryResponse(true, "调用成功", result)
            );

        } catch (Exception e) {
            log.error("工具调用失败", e);
            return ResponseEntity.ok(
                new McpQueryResponse(false, "调用失败: " + e.getMessage(), null)
            );
        }
    }

    /**
     * 简单查询接口（如果需要支持类似ChatClient的查询方式）
     */
    @PostMapping("/query")
    public ResponseEntity<McpQueryResponse> query(@RequestBody McpQueryRequest request) {
        try {
            log.info("收到查询请求: query={}", request.getQuery());

            if (request.getQuery() == null || request.getQuery().trim().isEmpty()) {
                return ResponseEntity.ok(
                    new McpQueryResponse(false, "查询内容不能为空", null)
                );
            }

            // 这里可以实现更复杂的查询逻辑
            // 例如：根据查询内容自动选择合适的工具
            // 目前返回提示信息
            return ResponseEntity.ok(
                new McpQueryResponse(
                    false, 
                    "请使用 /api/mcp/call 接口直接调用工具，或者实现查询逻辑", 
                    null
                )
            );

        } catch (Exception e) {
            log.error("查询失败", e);
            return ResponseEntity.ok(
                new McpQueryResponse(false, "查询失败: " + e.getMessage(), null)
            );
        }
    }

    /**
     * 获取指定工具的详细信息
     */
    @GetMapping("/tools/{toolName}")
    public ResponseEntity<McpQueryResponse> getToolInfo(@PathVariable String toolName) {
        try {
            if (!mcpToolService.isToolAvailable(toolName)) {
                return ResponseEntity.ok(
                    new McpQueryResponse(false, "工具不存在: " + toolName, null)
                );
            }

            var toolOpt = mcpToolService.getToolByName(toolName);
            if (toolOpt.isEmpty()) {
                return ResponseEntity.ok(
                    new McpQueryResponse(false, "工具不存在", null)
                );
            }

            MySyncMcpToolCallback tool = toolOpt.get();
            ToolInfo toolInfo = new ToolInfo(
                tool.getToolName(),
                tool.getToolDescription(),
                tool.getTool().inputSchema()
            );

            return ResponseEntity.ok(
                new McpQueryResponse(true, "获取工具信息成功", toolInfo)
            );

        } catch (Exception e) {
            log.error("获取工具信息失败", e);
            return ResponseEntity.ok(
                new McpQueryResponse(false, "获取失败: " + e.getMessage(), null)
            );
        }
    }
}

