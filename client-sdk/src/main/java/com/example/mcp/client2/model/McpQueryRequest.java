package com.example.mcp.client2.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map;

/**
 * MCP查询请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class McpQueryRequest {

    /**
     * 工具名称
     */
    @JsonProperty("toolName")
    private String toolName;

    /**
     * 工具参数
     */
    @JsonProperty("arguments")
    private Map<String, Object> arguments;

    /**
     * 查询内容（可选，用于简单查询）
     */
    @JsonProperty("query")
    private String query;
}

